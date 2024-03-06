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
import static by.mrrockka.repo.finalplaces.FinaleColumnNames.POSITION;

@Component
@RequiredArgsConstructor
class FinalePlacesEntityResultSetExtractor implements ResultSetExtractor<Optional<FinalePlacesEntity>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  @SneakyThrows
  public Optional<FinalePlacesEntity> extractData(final ResultSet rs) throws DataAccessException {
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
  private Map<Integer, PersonEntity> extractPlaces(final ResultSet rs) {
    final var places = new HashMap<Integer, PersonEntity>();
    do {
      places.put(rs.getInt(POSITION), personEntityRowMapper.mapRow(rs, rs.getRow()));
    } while (rs.next());

    return places;
  }

}
