package by.mrrockka.repo.finalplaces;

import by.mrrockka.repo.person.PersonEntity;
import by.mrrockka.repo.person.PersonEntityRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.finalplaces.FinaleColumnNames.GAME_ID;
import static by.mrrockka.repo.finalplaces.FinaleColumnNames.PLACE;

@Component
@RequiredArgsConstructor
public class FinalePlacesEntityResultSetExtractor implements ResultSetExtractor<Optional<FinalePlacesEntity>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  @SneakyThrows
  public Optional<FinalePlacesEntity> extractData(ResultSet rs) throws DataAccessException {
    if (rs.next()) {
      final var gameId = UUID.fromString(rs.getString(GAME_ID));
      return Optional.of(FinalePlacesEntity.builder()
                           .gameId(gameId)
                           .places(extractPlaces(rs))
                           .build());
    }
    return Optional.empty();
  }


  @SneakyThrows
  public Map<Integer, PersonEntity> extractPlaces(ResultSet rs) {
    HashMap<Integer, PersonEntity> places = new HashMap<>();
    do {
      places.put(rs.getInt(PLACE), personEntityRowMapper.mapRow(rs, rs.getRow()));
    } while (rs.next());

    return places;
  }

}
