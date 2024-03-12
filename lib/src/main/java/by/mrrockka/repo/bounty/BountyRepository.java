package by.mrrockka.repo.bounty;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static by.mrrockka.repo.bounty.BountyColumnNames.*;

@Repository
@RequiredArgsConstructor
public class BountyRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final BountyEntityListResultSetExtractor bountyEntityListResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO bounty
      (game_id, to_person, from_person, amount, created_at)
    VALUES
      (:game_id, :to_person, :from_person :amount, :created_at);
    """;

  public void save(final UUID gameId, final UUID fromPersonId, final UUID toPersonId, final BigDecimal amount,
                   final Instant createdAt) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId)
      .addValue(FROM_PERSON, fromPersonId)
      .addValue(TO_PERSON, toPersonId)
      .addValue(AMOUNT, amount)
      .addValue(CREATED_AT, Timestamp.from(createdAt));
    jdbcTemplate.update(SAVE_SQL, params);
  }

  private static final String FIND_ALL_BY_GAME_SQL = """
    SELECT
      b.game_id, b.amount,
      fp.id as from_person_id, fp.first_name as from_person_first_name, fp.last_name as from_person_last_name, fp.nick_name as from_person_nick_name,
      tp.id as to_person_id, tp.first_name as to_person_first_name, tp.last_name as to_person_last_name, tp.nick_name as to_person_nick_name
    FROM
      bounty as b
    JOIN
      person as fp on fp.id = b.from_person
    JOIN
      person as tp on tp.id = b.to_person
    WHERE
      game_id = :game_id;
    """;

  public List<BountyEntity> findAllByGameId(final UUID gameId) {
    final MapSqlParameterSource params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId);
    return jdbcTemplate.query(FIND_ALL_BY_GAME_SQL, params, bountyEntityListResultSetExtractor);
  }

}
