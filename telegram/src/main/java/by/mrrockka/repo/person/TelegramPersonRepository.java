package by.mrrockka.repo.person;

import by.mrrockka.domain.TelegramPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.*;

@Repository
@RequiredArgsConstructor
public class TelegramPersonRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final TelegramPersonEntityRowMapper telegramPersonEntityRowMapper;
  private final ChatIdToPersonEntityResultSetExtractor chatIdToPersonEntityResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO chat_persons
      (person_id, chat_id)
    VALUES
      (:person_id, :chat_id);
    """;

  public void save(final TelegramPerson person) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.PERSON_ID, person.getId())
      .addValue(CHAT_ID, person.getChatId());
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(final List<TelegramPerson> telegramPersons) {
    telegramPersons.forEach(this::save);
  }

  private static final String FIND_BY_CHAT_ID_AND_NICKNAMES_SQL = """
      SELECT
        cp.chat_id, p.id, p.first_name, p.last_name, p.nick_name
      FROM
        chat_persons as cp
      JOIN
        person as p on p.id = person_id
      WHERE
        chat_id = :chat_id AND
        p.nick_name IN (:nick_name)
    """;

  public List<TelegramPersonEntity> findAllByChatIdAndNicknames(final Long chatId, final List<String> telegrams) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(NICK_NAME, telegrams);

    return jdbcTemplate.query(FIND_BY_CHAT_ID_AND_NICKNAMES_SQL, params, telegramPersonEntityRowMapper);
  }

  private static final String FIND_BY_NICKNAME_SQL = """
      SELECT
        cp.chat_id, p.id, p.first_name, p.last_name, p.nick_name
      FROM
        chat_persons as cp
      JOIN
        person as p on p.id = person_id
      WHERE
        chat_id = :chat_id AND
        nick_name = :nick_name
    """;

  public Optional<TelegramPersonEntity> findByNickname(final Long chatId, final String nickname) {
    final var params = new MapSqlParameterSource()
      .addValue(NICK_NAME, nickname)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_BY_NICKNAME_SQL, params, rs -> rs.next()
      ? Optional.ofNullable(telegramPersonEntityRowMapper.mapRow(rs, rs.getRow()))
      : Optional.empty());
  }

  private static final String FIND_NOT_EXISTING_IDS_SQL = """
    WITH new_persons AS (
        SELECT v.id id
        FROM (VALUES :id) v(id)
        WHERE NOT EXISTS (SELECT 1 FROM person p WHERE p.id = v.id)
    ), new_for_chat AS (
        SELECT v.id id
        FROM (VALUES :id) v(id)
        WHERE NOT EXISTS (SELECT 1 FROM chat_persons cp WHERE cp.person_id = v.id AND cp.chat_id = :chat_id)
    )
    SELECT DISTINCT COALESCE(np.id, nfc.id), cp.chat_id
    FROM new_persons np
    RIGHT JOIN new_for_chat nfc ON nfc.id = np.id
    LEFT JOIN chat_persons cp ON cp.person_id = nfc.id
    ;
    """;

  public Map<UUID, Long> findNewPersonIds(final List<UUID> personIds, final Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(ID, personIds);

    return jdbcTemplate.query(FIND_NOT_EXISTING_IDS_SQL, params,
                              (rs, rowNum) -> Map.entry(rs.getObject(ID, UUID.class), rs.getLong(CHAT_ID)))
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static final String FIND_NOT_EXISTING_NICK_NAMES_SQL = """
    WITH new_persons AS (
        SELECT v.nick
        FROM (VALUES nick_name) v(nick)
        WHERE NOT EXISTS (SELECT 1 FROM person p WHERE p.nick_name = v.nick)
    ), new_for_chat AS (
        SELECT p.id, v.nick
        FROM (VALUES :nick_name) v(nick)
        JOIN person p ON p.nick_name = v.nick
        WHERE NOT EXISTS (SELECT 1 FROM chat_persons cp WHERE cp.person_id = p.id AND cp.chat_id = :chat_id)
    )
    select nick, null chat_id
    from new_persons np
    union
    select nfc.nick, cp.chat_id from new_for_chat nfc
    left join chat_persons cp on cp.person_id = nfc.id
    """;

  public Map<UUID, Long> findNewPersonIds(final List<UUID> personIds, final Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(ID, personIds);

    return jdbcTemplate.query(FIND_NOT_EXISTING_IDS_SQL, params,
                              (rs, rowNum) -> Map.entry(rs.getObject(ID, UUID.class), rs.getLong(CHAT_ID)))
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
