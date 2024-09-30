package by.mrrockka.repo.person;

import by.mrrockka.domain.TelegramPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.*;

@Repository
@RequiredArgsConstructor
public class TelegramPersonRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final TelegramPersonEntityRowMapper telegramPersonEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO chat_persons
      (person_id, chat_id)
    VALUES
      (:person_id, :chat_id);
    """;

  public void save(final TelegramPerson person) {
    save(person.getId(), person.getChatId());
  }

  public void save(final UUID personId, final Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(PERSON_ID, personId)
      .addValue(CHAT_ID, chatId);
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(final List<UUID> personIds, final Long chatId) {
    personIds.forEach(id -> save(id, chatId));
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

  public List<TelegramPersonEntity> findAllByChatIdAndNicknames(final List<String> nicknames, final Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(NICK_NAME, nicknames);

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

  public List<UUID> findNotExistentInChat(final List<String> nicknames, final Long chatId) {
    final var sql = STR."""
    WITH new_for_chat AS (
        SELECT p.id, v.nick_name
        FROM (VALUES \{nicknames.stream().map("('%s')"::formatted).reduce("%s,%s"::formatted).orElse("")}) v(nick_name)
        LEFT JOIN person p ON p.nick_name = v.nick_name
        WHERE NOT EXISTS (SELECT 1 FROM chat_persons cp WHERE cp.person_id = p.id AND cp.chat_id = \{chatId})
    )
    SELECT nfc.id
    FROM new_for_chat nfc
    """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getObject(ID, UUID.class))
      .stream()
      .toList();
  }
}
