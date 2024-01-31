package by.mrrockka.repo;

import by.mrrockka.repo.entities.GameEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GameRepository {

  private final JdbcTemplate jdbcTemplate;

  private static final String SAVE_SQL = """
    INSERT INTO game (id, chat_id, game_type, buy_in, stack, bounty)
    VALUES (:id, :chatId, :gameType, :buyIn, :stack, :bounty, :createdAt);
    """;

  public void save(GameEntity gameEntity) {
    final var params = new MapSqlParameterSource()
      .addValue("id", gameEntity.id())
      .addValue("chatId", gameEntity.chatId())
      .addValue("gameType", gameEntity.gameType().name())
      .addValue("buyIn", gameEntity.buyIn())
      .addValue("stack", gameEntity.stack())
      .addValue("bounty", gameEntity.bounty())
      .addValue("createdAt", Timestamp.valueOf(LocalDateTime.now()));
    jdbcTemplate.update(SAVE_SQL, params);
  }


  private static final String FIND_BY_ID_SQL = """
      SELECT
        id, chat_id, game_type, buy_in, stack, bounty
      FROM
        game
      WHERE
        id = ':id'
        AND chat_id = ':chat_id';
    """;

  public GameEntity findById(UUID id, String chatId) {
    final var params = new MapSqlParameterSource()
      .addValue("id", id)
      .addValue("chat_id", chatId);

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, GameEntity.class, params);
  }

}
