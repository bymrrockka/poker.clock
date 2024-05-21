package by.mrrockka.repo.moneytransfer;

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

@Repository
@RequiredArgsConstructor
public class MoneyTransferRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

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

}
