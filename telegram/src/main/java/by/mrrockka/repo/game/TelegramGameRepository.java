package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

import static by.mrrockka.repo.game.TelegramGameColumnNames.*;

@Repository
@RequiredArgsConstructor
public class TelegramGameRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final TelegramGameEntityResultSetExtractor telegramGameEntityResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO chat_games
      (game_id, chat_id, created_at, message_id)
    VALUES
      (:game_id, :chat_id, :created_at, :message_id);
    """;

  public void save(final TelegramGameEntity telegramGameEntity) {
    final var params = new MapSqlParameterSource()
      .addValue(GAME_ID, telegramGameEntity.gameId())
      .addValue(CHAT_ID, telegramGameEntity.chatId())
      .addValue(MESSAGE_ID, telegramGameEntity.messageId())
      .addValue(CREATED_AT, Timestamp.from(telegramGameEntity.createdAt()));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_CHAT_AND_MESSAGE_ID_SQL = """
      SELECT
        game_id, chat_id, created_at, message_id
      FROM
        chat_games
      WHERE
        chat_id = :chat_id AND
        message_id = :message_id
    """;

  public Optional<TelegramGameEntity> findByChatAndMessageId(final Long chatId, final Integer messageId) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId)
      .addValue(MESSAGE_ID, messageId);

    return jdbcTemplate.query(FIND_BY_CHAT_AND_MESSAGE_ID_SQL, params, telegramGameEntityResultSetExtractor);
  }

  private static final String FIND_LATEST_BY_CHAT_ID_SQL = """
      SELECT
        game_id, chat_id, created_at, message_id
      FROM
        chat_games
      WHERE
        chat_id = :chat_id
      ORDER BY created_at desc
      limit 1;
    """;

  public Optional<TelegramGameEntity> findLatestByChatId(final Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.query(FIND_LATEST_BY_CHAT_ID_SQL, params, telegramGameEntityResultSetExtractor);
  }

}
