package by.mrrockka.repo.withdrawals;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class WithdrawalsEntityListResultSetExtractor implements ResultSetExtractor<List<WithdrawalsEntity>> {

  private final WithdrawalsEntityResultSetExtractor withdrawalsEntityResultSetExtractor;

  @Override
  public List<WithdrawalsEntity> extractData(final ResultSet rs) throws SQLException, DataAccessException {
    final List<WithdrawalsEntity> withdrawalsEntities = new ArrayList<>();
    final var hasNext = rs.next();
    while (hasNext && !rs.isAfterLast()) {
      withdrawalsEntities.add(withdrawalsEntityResultSetExtractor.assembleEntity(rs));
    }
    return withdrawalsEntities;
  }
}
