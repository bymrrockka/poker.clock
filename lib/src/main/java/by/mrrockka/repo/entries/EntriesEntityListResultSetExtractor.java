package by.mrrockka.repo.entries;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Component
@RequiredArgsConstructor
class EntriesEntityListResultSetExtractor implements ResultSetExtractor<List<EntriesEntity>> {

  private final EntriesEntityResultSetExtractor entityResultSetExtractor;

  @Override
  public List<EntriesEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    List<EntriesEntity> entriesEntities = new ArrayList<>();
    final var hasNext = rs.next();
    while (hasNext && !rs.isAfterLast()) {
      entriesEntities.add(entityResultSetExtractor.assembleEntity(rs));
    }
    return entriesEntities;
  }
}
