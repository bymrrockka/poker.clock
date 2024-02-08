package by.mrrockka.repo.entry;

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

import static by.mrrockka.repo.entry.EntryColumnNames.*;

@Component
@RequiredArgsConstructor
public class EntriesEntityResultSetExtractor implements ResultSetExtractor<Optional<EntriesEntity>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public Optional<EntriesEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    if (rs.next()) {
      return Optional.of(
        EntriesEntity.builder()
          .gameId(UUID.fromString(rs.getString(GAME_ID)))
          .personId(UUID.fromString(rs.getString(PERSON_ID)))
          .amounts(extractAmounts(rs))
          .build());
    }
    return Optional.empty();
  }

  private List<BigDecimal> extractAmounts(ResultSet rs) throws SQLException {
    List<BigDecimal> amounts = new ArrayList<>();
    do {
      amounts.add(rs.getBigDecimal(AMOUNT));
    } while (rs.next());

    return amounts;
  }
}
