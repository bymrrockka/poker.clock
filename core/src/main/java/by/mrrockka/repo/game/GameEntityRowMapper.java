package by.mrrockka.repo.game;

import by.mrrockka.domain.GameType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.game.GameColumnNames.*;

@Component
class GameEntityRowMapper implements RowMapper<GameEntity> {

  @Override
  public GameEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    return GameEntity.builder()
      .id(UUID.fromString(rs.getString(ID)))
      .gameType(GameType.valueOf(rs.getString(GAME_TYPE)))
      .stack(rs.getBigDecimal(STACK))
      .buyIn(rs.getBigDecimal(BUY_IN))
      .bounty(rs.getBigDecimal(BOUNTY))
      .finishedAt(Optional.ofNullable(rs.getTimestamp(FINISHED_AT))
                    .map(Timestamp::toInstant)
                    .orElse(null))
      .build();
  }
}
