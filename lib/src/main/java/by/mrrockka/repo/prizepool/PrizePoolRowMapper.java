package by.mrrockka.repo.prizepool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static by.mrrockka.repo.prizepool.PrizePoolColumnNames.GAME_ID;
import static by.mrrockka.repo.prizepool.PrizePoolColumnNames.SCHEMA;

@Component
@RequiredArgsConstructor
public class PrizePoolRowMapper implements RowMapper<PrizePoolEntity> {

  private final ObjectMapper objectMapper;

  @Override
  @SneakyThrows
  public PrizePoolEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    final var typeRef = new TypeReference<Map<Integer, BigDecimal>>() {};
    return PrizePoolEntity.builder()
      .gameId(UUID.fromString(rs.getString(GAME_ID)))
      .schema(objectMapper.readValue(rs.getString(SCHEMA), typeRef))
      .build();
  }

}
