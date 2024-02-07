package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static by.mrrockka.repo.game.GameColumnNames.*;

@Repository
@RequiredArgsConstructor
public class GameRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final GameEntityRowMapper gameEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO game
      (id, chat_id, game_type, buy_in, stack, bounty, created_at)
    VALUES
      (:id, :chat_id, :game_type, :buy_in, :stack, :bounty, :created_at);
    """;

  public void save(GameEntity gameEntity) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, gameEntity.id())
      .addValue(CHAT_ID, gameEntity.chatId())
      .addValue(GAME_TYPE, gameEntity.gameType().name())
      .addValue(BUY_IN, gameEntity.buyIn())
      .addValue(STACK, gameEntity.stack())
      .addValue(BOUNTY, gameEntity.bounty())
      .addValue(CREATED_AT, Timestamp.valueOf(LocalDateTime.now()));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_ID_SQL = """
      SELECT
        id, chat_id, game_type, buy_in, stack, bounty
      FROM
        game
      WHERE
        id = :id AND
        chat_id = :chat_id;
    """;

  public GameEntity findById(UUID id, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, id)
      .addValue(CHAT_ID, chatId);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, gameEntityRowMapper);
  }

}
