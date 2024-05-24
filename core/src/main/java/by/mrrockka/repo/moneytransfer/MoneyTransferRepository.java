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
import java.util.UUID;

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


  private static final String GET_GLOBAL_PERSON_STATISTICS_SQL = """
    select
        mt.person_id,
        count(mt.game_id) as games_played,
        sum(g.buy_in) + sum(g.bounty) as money_in,
        sum(won.total) as money_won,
        sum(lose.total) as money_lose,
        sum(tonfp.times_on_first_place) as times_on_first_place,
        count(fp.person_id) as times_in_prizes
    from money_transfer as mt

    left join (
        select person_id as pid, sum(amount) as total
        from money_transfer
        where type = 'CREDIT'
        group by person_id
        ) as won on won.pid = mt.person_id

    left join (
        select person_id as pid, sum(amount) as total
        from money_transfer
        where type = 'DEBIT'
        group by person_id
        ) as lose on lose.pid = mt.person_id

    left join game as g on g.id = mt.game_id

    left join (
        select person_id as pid, count(position) as times_on_first_place
        from finale_places
        where position = 1
        group by person_id
        ) as tonfp on tonfp.pid = mt.person_id

    left join finale_places as fp on fp.person_id = mt.person_id

    where mt.person_id = :person_id
    group by mt.person_id;
    """;

  public void getPersonGlobalStatistics(final UUID personId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(MoneyTransferColumnNames.PERSON_ID, personId);
    jdbcTemplate.queryForObject(GET_GLOBAL_PERSON_STATISTICS_SQL, params, (rs, rowNum) -> personId); //todo:
  }

}
