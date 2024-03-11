package by.mrrockka.repo.withdrawals;

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
class WithdrawalsEntityResultSetExtractor implements ResultSetExtractor<Optional<WithdrawalsEntity>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public Optional<WithdrawalsEntity> extractData(final ResultSet rs) throws SQLException, DataAccessException {
    if (rs.next()) {
      return Optional.of(assembleEntity(rs));
    }
    return Optional.empty();
  }

  public WithdrawalsEntity assembleEntity(final ResultSet rs) throws SQLException {
    final var person = personEntityRowMapper.mapRow(rs, rs.getRow());
    return WithdrawalsEntity.builder()
      .gameId(UUID.fromString(rs.getString(WithdrawalColumnNames.GAME_ID)))
      .person(person)
      .amounts(extractAmounts(rs, person.getId()))
      .build();
  }

  private List<BigDecimal> extractAmounts(final ResultSet rs, final UUID personId) throws SQLException {
    final List<BigDecimal> amounts = new ArrayList<>();
    do {
      amounts.add(rs.getBigDecimal(WithdrawalColumnNames.AMOUNT));
    } while (rs.next() && rs.getString(WithdrawalColumnNames.PERSON_ID).equals(personId.toString()));

    return amounts;
  }
}
