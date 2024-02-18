package by.mrrockka.repo.game;

import by.mrrockka.domain.game.GameType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static by.mrrockka.repo.game.GameColumnNames.*;

@Component
class GameEntityRowMapper implements RowMapper<GameEntity> {

  @Override
  public GameEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    return GameEntity.builder()
      .id(UUID.fromString(rs.getString(ID)))
      .gameType(GameType.valueOf(rs.getString(GAME_TYPE)))
      .stack(rs.getBigDecimal(STACK))
      .buyIn(rs.getBigDecimal(BUY_IN))
      .bounty(rs.getBigDecimal(BOUNTY))
      .build();
  }
}
