package by.mrrockka.repo.entries;

import by.mrrockka.repo.person.PersonEntityRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class EntriesEntityResultSetExtractor implements ResultSetExtractor<Optional<EntriesEntity>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public Optional<EntriesEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    if (rs.next()) {
      return Optional.of(assembleEntity(rs));
    }
    return Optional.empty();
  }

  public EntriesEntity assembleEntity(ResultSet rs) throws SQLException {
    final var person = personEntityRowMapper.mapRow(rs, rs.getRow());
    return EntriesEntity.builder()
      .gameId(UUID.fromString(rs.getString(EntryColumnNames.GAME_ID)))
      .person(person)
      .amounts(extractAmounts(rs, person.id()))
      .build();
  }

  private List<BigDecimal> extractAmounts(ResultSet rs, UUID personId) throws SQLException {
    List<BigDecimal> amounts = new ArrayList<>();
    do {
      amounts.add(rs.getBigDecimal(EntryColumnNames.AMOUNT));
    } while (rs.next() && rs.getString(EntryColumnNames.PERSON_ID).equals(personId.toString()));

    return amounts;
  }
}
