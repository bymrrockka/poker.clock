package by.mrrockka.repo.person;

import by.mrrockka.domain.TelegramPerson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TelegramPersonRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  private static final String SAVE_SQL = """
    INSERT INTO chat_persons
      (person_id, chat_id, telegram)
    VALUES
      (:person_id, :chat_id, :telegram);
    """;

  public void save(TelegramPerson person) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.PERSON_ID, person.getId())
      .addValue(TelegramPersonColumnNames.CHAT_ID, person.chatId())
      .addValue(TelegramPersonColumnNames.TELEGRAM, person.telegram());
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void saveAll(List<TelegramPerson> telegramPersons) {
    telegramPersons.forEach(this::save);
  }

  private static final String FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL = """
      SELECT
        person_id, telegram
      FROM
        chat_persons
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<Pair<UUID, String>> findByChatIdAndTelegrams(Long chatId, List<String> telegrams) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.CHAT_ID, chatId)
      .addValue(TelegramPersonColumnNames.TELEGRAM, telegrams);

    return jdbcTemplate.query(FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL, params, (rs, rowNum) ->
      Pair.of(rs.getObject(1, UUID.class), rs.getString(2)));
  }
  /* todo: move to service

  private static final String FIND_BY_TELEGRAM_SQL = """
      SELECT
        id, first_name, last_name
      FROM person
      WHERE
        chat_id = :chat_id AND
        telegram = :telegram
    """;

  public PersonEntity findByTelegram(String telegram, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegram)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.queryForObject(FIND_BY_TELEGRAM_SQL, params, personEntityRowMapper);
  }

  private static final String FIND_ALL_BY_TELEGRAM_SQL = """
      SELECT
         id, chat_id, telegram, first_name, last_name
      FROM person
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<PersonEntity> findAllByTelegrams(List<String> telegrams, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TELEGRAM, telegrams)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_ALL_BY_TELEGRAM_SQL, params, personEntityRowMapper);
  }
*/


}
