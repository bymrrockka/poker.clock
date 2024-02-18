package by.mrrockka.repo.entries;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EntriesRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final EntriesEntityResultSetExtractor entityResultSetExtractor;
  private final EntriesEntityListResultSetExtractor entriesEntityListResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO entries
      (game_id, person_id, amount, created_at)
    VALUES
      (:game_id, :person_id, :amount, :created_at);
    """;

  public void save(UUID gameId, UUID personId, BigDecimal amount) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(EntryColumnNames.GAME_ID, gameId)
      .addValue(EntryColumnNames.PERSON_ID, personId)
      .addValue(EntryColumnNames.AMOUNT, amount)
      .addValue(EntryColumnNames.CREATED_AT, Timestamp.valueOf(LocalDateTime.now()));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_ALL_BY_GAME_AND_PERSON_SQL = """
    SELECT
      e.game_id, e.amount, e.person_id, p.id, p.first_name, p.last_name
    FROM
      entries as e
    JOIN
      person as p on p.id = e.person_id
    WHERE
      game_id = :game_id AND
      person_id = :person_id
    """;

  public Optional<EntriesEntity> findByGameAndPerson(UUID gameId, UUID personId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(EntryColumnNames.GAME_ID, gameId)
      .addValue(EntryColumnNames.PERSON_ID, personId);
    return jdbcTemplate.query(FIND_ALL_BY_GAME_AND_PERSON_SQL, params, entityResultSetExtractor);
  }

  private static final String FIND_ALL_BY_GAME_SQL = """
    SELECT
      e.game_id, e.amount, e.person_id, p.id, p.first_name, p.last_name
    FROM
      entries as e
    JOIN
      person as p on p.id = e.person_id
    WHERE
      game_id = :game_id
    """;

  //  todo: add int test
  public List<EntriesEntity> findAllByGameId(UUID gameId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(EntryColumnNames.GAME_ID, gameId);
    return jdbcTemplate.query(FIND_ALL_BY_GAME_SQL, params, entriesEntityListResultSetExtractor);
  }

}
