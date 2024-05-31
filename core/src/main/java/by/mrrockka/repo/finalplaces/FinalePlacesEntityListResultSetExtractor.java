package by.mrrockka.repo.finalplaces;

import by.mrrockka.repo.person.PersonEntity;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static by.mrrockka.repo.finalplaces.FinaleColumnNames.*;

@Component
class FinalePlacesEntityListResultSetExtractor implements ResultSetExtractor<List<FinalePlacesEntity>> {

  @Override
  @SneakyThrows
  public List<FinalePlacesEntity> extractData(final ResultSet rs) throws DataAccessException {
    final List<FinalePlacesEntity> finalePlacesEntities = new ArrayList<>();
    while (rs.next()) {
      final var personEntity = PersonEntity.personBuilder().id(rs.getObject(PERSON_ID, UUID.class)).build();
      finalePlacesEntities.add(
        FinalePlacesEntity.builder()
          .gameId(rs.getObject(GAME_ID, UUID.class))
          .places(Map.of(rs.getInt(POSITION), personEntity))
          .build()
      );
    }
    return finalePlacesEntities;
  }
}
