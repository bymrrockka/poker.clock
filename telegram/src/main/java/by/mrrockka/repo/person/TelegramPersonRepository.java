package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//todo: add int tests
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

  public void save(UUID personId, String chatId, String telegram) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.PERSON_ID, personId)
      .addValue(TelegramPersonColumnNames.CHAT_ID, chatId)
      .addValue(TelegramPersonColumnNames.TELEGRAM, telegram);
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL = """
      SELECT
        person_id
      FROM
        chat_persons
      WHERE
        chat_id = :chat_id AND
        telegram IN (:telegram)
    """;

  public List<UUID> findByChatIdAndTelegrams(String chatId, List<String> telegrams) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramPersonColumnNames.CHAT_ID, chatId)
      .addValue(TelegramPersonColumnNames.TELEGRAM, telegrams);

    return jdbcTemplate.queryForList(FIND_BY_CHAT_ID_AND_TELEGRAMS_SQL, params, UUID.class);
  }

}
