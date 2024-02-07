package by.mrrockka.repo.finalplaces;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.finalplaces.FinaleColumnNames.*;

@Repository
@RequiredArgsConstructor
public class FinalePlacesRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final FinalePlacesEntityResultSetExtractor finalePlacesEntityResultSetExtractor;

  private static final String SAVE_SQL = """
    INSERT INTO finale_places
      (game_id, person_id, place)
    VALUES
      (:game_id, :person_id, :place);
    """;

  @SneakyThrows
  public void save(FinalePlacesEntity finalePlacesEntity) {
    finalePlacesEntity.places().entrySet()
      .stream()
      .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().id()))
      .forEach(entry -> {
        final MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue(GAME_ID, finalePlacesEntity.gameId())
          .addValue(PERSON_ID, entry.getValue())
          .addValue(PLACE, entry.getKey());

        jdbcTemplate.update(SAVE_SQL, params);
      });
  }

  private static final String FIND_BY_GAME_ID_SQL = """
    SELECT
      f.game_id, f.place, p.id, p.chat_id, p.telegram, p.first_name, p.last_name
    FROM
     finale_places as f
    JOIN person as p ON f.person_id = p.id
    WHERE
      f.game_id = :game_id;
    """;

  public Optional<FinalePlacesEntity> findByGameId(UUID gameId) {
    final var params = new MapSqlParameterSource()
      .addValue(GAME_ID, gameId);

    return jdbcTemplate.query(FIND_BY_GAME_ID_SQL, params, finalePlacesEntityResultSetExtractor);
  }
}
