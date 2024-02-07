package by.mrrockka.repo.game;

import by.mrrockka.domain.game.GameType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GameRepository {

  private static final String ID = "id";
  private static final String CHAT_ID = "chat_id";
  private static final String GAME_TYPE = "game_type";
  private static final String BUY_IN = "buy_in";
  private static final String STACK = "stack";
  private static final String BOUNTY = "bounty";
  private static final String CREATED_AT = "created_at";

  private final NamedParameterJdbcTemplate jdbcTemplate;

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

    return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, params, (rs, rowNum) ->
      GameEntity.builder()
                .id(UUID.fromString(rs.getString(ID)))
                .chatId(rs.getString(CHAT_ID))
                .gameType(GameType.valueOf(rs.getString(GAME_TYPE)))
                .stack(rs.getBigDecimal(STACK))
                .buyIn(rs.getBigDecimal(BUY_IN))
                .bounty(rs.getBigDecimal(BOUNTY))
                .build());
  }

}
