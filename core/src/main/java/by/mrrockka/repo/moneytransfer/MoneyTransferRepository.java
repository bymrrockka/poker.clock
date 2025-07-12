package by.mrrockka.repo.moneytransfer;

import by.mrrockka.domain.MoneyTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MoneyTransferRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final MoneyTransferEntityListResultSetExtractor moneyTransferEntityListResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO money_transfer
      (game_id, person_id, amount, type, created_at, updated_at)
    VALUES
      (:game_id, :person_id, :amount, :type, :created_at, :updated_at)
    ON CONFLICT (game_id, person_id) DO UPDATE
    SET amount = :amount,
      type = :type,
      updated_at = :updated_at;
    """;


  public void save(final MoneyTransfer moneyTransfer, final Instant createdAt) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(MoneyTransferColumnNames.GAME_ID, moneyTransfer.gameId())
      .addValue(MoneyTransferColumnNames.PERSON_ID, moneyTransfer.personId())
      .addValue(MoneyTransferColumnNames.AMOUNT, moneyTransfer.amount())
      .addValue(MoneyTransferColumnNames.TYPE, moneyTransfer.type().name())
      .addValue(MoneyTransferColumnNames.CREATED_AT, Timestamp.from(createdAt))
      .addValue(MoneyTransferColumnNames.UPDATED_AT, Timestamp.from(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  public void save(final MoneyTransferEntity moneyTransferEntity, final Instant createdAt) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(MoneyTransferColumnNames.GAME_ID, moneyTransferEntity.gameId())
      .addValue(MoneyTransferColumnNames.PERSON_ID, moneyTransferEntity.personId())
      .addValue(MoneyTransferColumnNames.AMOUNT, moneyTransferEntity.amount())
      .addValue(MoneyTransferColumnNames.TYPE, moneyTransferEntity.type().name())
      .addValue(MoneyTransferColumnNames.CREATED_AT, Timestamp.from(createdAt))
      .addValue(MoneyTransferColumnNames.UPDATED_AT, Timestamp.from(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public void saveAll(final List<MoneyTransferEntity> payoutsEntities, final Instant createdAt) {
    payoutsEntities.forEach(entity -> save(entity, createdAt));
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public void saveAll(final List<MoneyTransfer> moneyTransfers) {
    Instant createdAt = Instant.now();
    moneyTransfers.forEach(entity -> save(entity, createdAt));
  }

  private static final String GET_PERSON_MONEY_TRANSFERS_SQL = """
      SELECT
          person_id, game_id, amount, type
      FROM money_transfer
      WHERE person_id = :person_id;
    """;

  public List<MoneyTransferEntity> getForPerson(final UUID personId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(MoneyTransferColumnNames.PERSON_ID, personId);
    return jdbcTemplate.query(GET_PERSON_MONEY_TRANSFERS_SQL, params, moneyTransferEntityListResultSetExtractor);
  }

}
