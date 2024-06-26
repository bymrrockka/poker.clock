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

import static by.mrrockka.repo.person.TelegramPersonColumnNames.CHAT_ID;
import static by.mrrockka.repo.person.TelegramPersonColumnNames.NICK_NAME;

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

}
