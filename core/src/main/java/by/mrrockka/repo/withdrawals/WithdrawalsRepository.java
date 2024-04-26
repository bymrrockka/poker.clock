package by.mrrockka.repo.withdrawals;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static by.mrrockka.repo.withdrawals.WithdrawalColumnNames.*;

@Repository
@RequiredArgsConstructor
public class WithdrawalsRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final WithdrawalsEntityListResultSetExtractor entriesEntityListResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO withdrawal
      (game_id, person_id, amount, created_at)
    VALUES
      (:game_id, :person_id, :amount, :created_at);
    """;

  public void save(final UUID gameId, final UUID personId, final BigDecimal amount, final Instant createdAt) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId)
      .addValue(PERSON_ID, personId)
      .addValue(AMOUNT, amount)
      .addValue(CREATED_AT, Timestamp.from(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
  public void saveAll(final UUID gameId, final List<UUID> personIds, final BigDecimal amount, final Instant createdAt) {
    personIds.forEach(personId -> save(gameId, personId, amount, createdAt));
  }

  private static final String FIND_ALL_BY_GAME_SQL = """
    SELECT
      w.game_id, w.amount, w.person_id, p.id, p.first_name, p.last_name, p.nick_name
    FROM
      withdrawal as w
    JOIN
      person as p on p.id = w.person_id
    WHERE
      game_id = :game_id
    ORDER BY p.id, created_at ASC
    """;

  public List<WithdrawalsEntity> findAllByGameId(final UUID gameId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId);
    return jdbcTemplate.query(FIND_ALL_BY_GAME_SQL, params, entriesEntityListResultSetExtractor);
  }

}
