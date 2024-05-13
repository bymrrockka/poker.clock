package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static by.mrrockka.repo.game.GameColumnNames.*;

@Repository
@RequiredArgsConstructor
public class GameRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final GameEntityRowMapper gameEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO game
      (id, game_type, buy_in, stack, bounty, created_at)
    VALUES
      (:id, :game_type, :buy_in, :stack, :bounty, :created_at);
    """;

  public void save(final GameEntity gameEntity, final Instant createdAt) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, gameEntity.id())
      .addValue(GAME_TYPE, gameEntity.gameType().name())
      .addValue(BUY_IN, gameEntity.buyIn())
      .addValue(STACK, gameEntity.stack())
      .addValue(BOUNTY, gameEntity.bounty())
      .addValue(CREATED_AT, Timestamp.from(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FINISH_GAME_SQL = """
    UPDATE game
    SET
      finished_at = :finished_at
    WHERE
      game_id = :game_id;
    """;

  public void finishGame(final UUID gameId, final Instant finishedAt) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, gameId)
      .addValue(FINISHED_AT, Timestamp.from(finishedAt));
    jdbcTemplate.update(FINISH_GAME_SQL, params);
  }

  private static final String FIND_BY_ID_SQL = """
      SELECT
        id, game_type, buy_in, stack, bounty
      FROM
        game
      WHERE
        id = :id;
    """;

  public GameEntity findById(final UUID id) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, id);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, gameEntityRowMapper);
  }

}
