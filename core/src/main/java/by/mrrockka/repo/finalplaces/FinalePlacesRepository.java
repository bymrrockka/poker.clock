package by.mrrockka.repo.finalplaces;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FinalePlacesRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final FinalePlacesEntityResultSetExtractor finalePlacesEntityResultSetExtractor;
  private final FinalePlacesEntityListResultSetExtractor finalePlacesEntityListResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO finale_places
      (game_id, person_id, position)
    VALUES
      (:game_id, :person_id, :position);
    """;

  @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
  public void save(final FinalePlacesEntity finalePlacesEntity) {
    finalePlacesEntity.places().entrySet()
      .stream()
      .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getId()))
      .forEach(entry -> {
        final MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue(FinaleColumnNames.GAME_ID, finalePlacesEntity.gameId())
          .addValue(FinaleColumnNames.PERSON_ID, entry.getValue())
          .addValue(FinaleColumnNames.POSITION, entry.getKey());

        jdbcTemplate.update(SAVE_SQL, params);
      });
  }

  private static final String FIND_BY_GAME_ID_SQL = """
    SELECT
      f.game_id, f.position, p.id, p.first_name, p.last_name, p.nick_name
    FROM
     finale_places as f
    JOIN person as p ON f.person_id = p.id
    WHERE
      f.game_id = :game_id;
    """;

  public Optional<FinalePlacesEntity> findByGameId(final UUID gameId) {
    final var params = new MapSqlParameterSource()
      .addValue(FinaleColumnNames.GAME_ID, gameId);

    return jdbcTemplate.query(FIND_BY_GAME_ID_SQL, params, finalePlacesEntityResultSetExtractor);
  }

  private static final String FIND_ALL_BY_PERSON_ID_SQL = """
    SELECT
      f.game_id, f.position, f.person_id
    FROM
     finale_places as f
    WHERE
      f.person_id = :person_id;
    """;

  public List<FinalePlacesEntity> findAllByPersonId(final UUID personId) {
    final var params = new MapSqlParameterSource()
      .addValue(FinaleColumnNames.PERSON_ID, personId);

    return jdbcTemplate.query(FIND_ALL_BY_PERSON_ID_SQL, params, finalePlacesEntityListResultSetExtractor);
  }
}
