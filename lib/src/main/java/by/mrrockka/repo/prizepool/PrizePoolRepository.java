package by.mrrockka.repo.prizepool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PrizePoolRepository {

  private static final String GAME_ID = "game_id";
  private static final String SCHEMA = "schema";

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final ObjectMapper objectMapper;

  private static final String SAVE_SQL = """
    INSERT INTO prize_pool
      (game_id, schema)
    VALUES
      (:game_id, :schema::jsonb);
    """;

  @SneakyThrows
  public void save(PrizePoolEntity prizePoolEntity) {
    final MapSqlParameterSource params;
    params = new MapSqlParameterSource()
      .addValue(GAME_ID, prizePoolEntity.gameId())
      .addValue(SCHEMA, objectMapper.writeValueAsString(prizePoolEntity.schema()));

    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_GAME_ID_SQL = """
    SELECT
      game_id, schema
    FROM
     prize_pool
    WHERE
      game_id = :game_id;
    """;

  public PrizePoolEntity findByGameId(UUID gameId) {
    final var params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId);

    return jdbcTemplate.queryForObject(FIND_BY_GAME_ID_SQL, params, this::mapRows);
  }

  @SneakyThrows
  private PrizePoolEntity mapRows(ResultSet rs, int rowNum) {
    final var typeRef = new TypeReference<Map<Integer, BigDecimal>>() {};
    return PrizePoolEntity.builder()
      .gameId(UUID.fromString(rs.getString(GAME_ID)))
      .schema(objectMapper.readValue(rs.getString(SCHEMA), typeRef))
      .build();
  }

}
