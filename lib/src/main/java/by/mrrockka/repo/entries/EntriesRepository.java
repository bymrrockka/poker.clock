package by.mrrockka.repo.entries;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.entries.EntryColumnNames.*;

@Repository
@RequiredArgsConstructor
public class EntriesRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EntriesEntityResultSetExtractor entityResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO entries
      (game_id, person_id, amount, created_at)
    VALUES
      (:game_id, :person_id, :amount, :created_at);
    """;

  public void save(UUID gameId, UUID personId, BigDecimal amount) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId)
      .addValue(PERSON_ID, personId)
      .addValue(AMOUNT, amount)
      .addValue(CREATED_AT, Timestamp.valueOf(LocalDateTime.now()));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_BY_GAME_AND_PERSON_SQL = """
    SELECT
      game_id, amount, person_id
    FROM
      entries
    WHERE
      game_id = :game_id AND
      person_id = :person_id
    """;

  public Optional<EntriesEntity> findByGameAndPerson(UUID gameId, UUID personId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId)
      .addValue(PERSON_ID, personId);
    return jdbcTemplate.query(FIND_BY_GAME_AND_PERSON_SQL, params, entityResultSetExtractor);
  }

}
