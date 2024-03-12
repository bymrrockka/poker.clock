package by.mrrockka.repo.bounty;

import by.mrrockka.repo.person.PersonEntityRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.mrrockka.repo.bounty.BountyColumnNames.*;

@Component
@RequiredArgsConstructor
class BountyEntityListResultSetExtractor implements ResultSetExtractor<List<BountyEntity>> {
  private static final String DELIMITER = "_";

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public List<BountyEntity> extractData(final ResultSet rs) throws SQLException, DataAccessException {
    final List<BountyEntity> bountyEntities = new ArrayList<>();
    while (rs.next()) {
      bountyEntities.add(
        BountyEntity.builder()
          .gameId(rs.getObject(GAME_ID, UUID.class))
          .to(personEntityRowMapper.mapRow(TO_PERSON + DELIMITER, rs, rs.getRow()))
          .from(personEntityRowMapper.mapRow(FROM_PERSON + DELIMITER, rs, rs.getRow()))
          .amount(rs.getBigDecimal(AMOUNT))
          .build()
      );
    }
    return bountyEntities;
  }
}
