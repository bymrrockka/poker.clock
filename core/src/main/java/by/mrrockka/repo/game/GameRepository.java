package by.mrrockka.repo.game;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static by.mrrockka.repo.game.GameColumnNames.*;

@Repository
@RequiredArgsConstructor
public class GameRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final GameEntityRowMapper gameEntityRowMapper;

  private static final String SAVE_SQL = """
    INSERT INTO game
      (id, game_type, buy_in, stack, bounty)
    VALUES
      (:id, :game_type, :buy_in, :stack, :bounty);
    """;

  public void save(final GameEntity gameEntity) {
    final var params = new MapSqlParameterSource()
      .addValue(ID, gameEntity.id())
      .addValue(GAME_TYPE, gameEntity.gameType().name())
      .addValue(BUY_IN, gameEntity.buyIn())
      .addValue(STACK, gameEntity.stack())
      .addValue(BOUNTY, gameEntity.bounty());
    jdbcTemplate.update(SAVE_SQL, params);
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
