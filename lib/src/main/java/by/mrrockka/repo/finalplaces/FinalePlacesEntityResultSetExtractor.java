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
import java.util.UUID;

import static by.mrrockka.repo.finalplaces.FinaleColumnNames.GAME_ID;
import static by.mrrockka.repo.finalplaces.FinaleColumnNames.PLACE;

@Component
@RequiredArgsConstructor
public class FinalePlacesEntityResultSetExtractor implements ResultSetExtractor<FinalePlacesEntity> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  @SneakyThrows
  public FinalePlacesEntity extractData(ResultSet rs) throws DataAccessException {
    rs.next();
    final var gameId = UUID.fromString(rs.getString(GAME_ID));
    return FinalePlacesEntity.builder()
      .gameId(gameId)
      .places(extractPlaces(rs, gameId))
      .build();
  }


  @SneakyThrows
  public Map<Integer, PersonEntity> extractPlaces(ResultSet rs, UUID gameId) {
    HashMap<Integer, PersonEntity> places = new HashMap<>();

    do {
      places.put(rs.getInt(PLACE), personEntityRowMapper.mapRow(rs, rs.getRow()));
      rs.next();
    } while (!rs.isAfterLast() && gameId.equals(UUID.fromString(rs.getString(GAME_ID))));

    return places;
  }

}
