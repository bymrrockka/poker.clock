package by.mrrockka.repo.moneytransfer;

import by.mrrockka.domain.payout.TransferType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.mrrockka.repo.moneytransfer.MoneyTransferColumnNames.*;

@Component
class MoneyTransferEntityListResultSetExtractor implements ResultSetExtractor<List<MoneyTransferEntity>> {

  @Override
  public List<MoneyTransferEntity> extractData(final ResultSet rs) throws SQLException, DataAccessException {
    final List<MoneyTransferEntity> entriesEntities = new ArrayList<>();
    while (rs.next()) {
      entriesEntities.add(extractEntity(rs));
    }
    return entriesEntities;
  }

  private MoneyTransferEntity extractEntity(final ResultSet rs) throws SQLException {
    return MoneyTransferEntity.builder()
      .gameId(rs.getObject(GAME_ID, UUID.class))
      .personId(rs.getObject(PERSON_ID, UUID.class))
      .amount(rs.getBigDecimal(AMOUNT))
      .type(TransferType.valueOf(rs.getString(TYPE)))
      .build();
  }

}
