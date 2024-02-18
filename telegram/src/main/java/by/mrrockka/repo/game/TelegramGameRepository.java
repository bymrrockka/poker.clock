package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

//todo: add int tests

@Repository
@RequiredArgsConstructor
public class TelegramGameRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  private static final String SAVE_SQL = """
    INSERT INTO chat_games
      (game_id, chat_id, created_at)
    VALUES
      (:game_id, :chat_id, :created_at);
    """;

  public void save(UUID gameId, String chatId, LocalDateTime createdAt) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramGameColumnNames.GAME_ID, gameId)
      .addValue(TelegramGameColumnNames.CHAT_ID, chatId)
      .addValue(TelegramGameColumnNames.CREATED_AT, Timestamp.valueOf(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_CHAT_ID_AND_CREATED_AT_SQL = """
      SELECT
        game_id
      FROM
        chat_games
      WHERE
        chat_id = :chat_id AND
        created_at = :created_at
    """;

  public UUID findByChatIdAndCreatedAt(String chatId, LocalDateTime createdAt) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramGameColumnNames.CHAT_ID, chatId)
      .addValue(TelegramGameColumnNames.CREATED_AT, Timestamp.valueOf(createdAt));

    return jdbcTemplate.queryForObject(FIND_BY_CHAT_ID_AND_CREATED_AT_SQL, params, UUID.class);
  }

}
