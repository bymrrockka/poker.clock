package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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

  public void save(UUID gameId, Long chatId, Instant createdAt) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramGameColumnNames.GAME_ID, gameId)
      .addValue(TelegramGameColumnNames.CHAT_ID, chatId)
      .addValue(TelegramGameColumnNames.CREATED_AT, Timestamp.from(createdAt));
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

  public Optional<UUID> findByChatIdAndCreatedAt(Long chatId, Instant createdAt) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramGameColumnNames.CHAT_ID, chatId)
      .addValue(TelegramGameColumnNames.CREATED_AT, Timestamp.from(createdAt));

    return jdbcTemplate.queryForObject(FIND_BY_CHAT_ID_AND_CREATED_AT_SQL, params, mapOptionalUuid());
  }

  private static final String FIND_LATEST_BY_CHAT_ID_SQL = """
      SELECT
        game_id
      FROM
        chat_games
      WHERE
        chat_id = :chat_id
      ORDER BY created_at desc
      limit 1;
    """;

  public Optional<UUID> findLatestByChatId(Long chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(TelegramGameColumnNames.CHAT_ID, chatId);

    return jdbcTemplate.queryForObject(FIND_LATEST_BY_CHAT_ID_SQL, params, mapOptionalUuid());
  }

  private RowMapper<Optional<UUID>> mapOptionalUuid() {
    return (rs, rowNum) -> Optional.ofNullable(rs.getObject(1, UUID.class));
  }

}
