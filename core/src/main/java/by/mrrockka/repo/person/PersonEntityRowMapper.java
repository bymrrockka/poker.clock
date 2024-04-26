package by.mrrockka.repo.person;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static by.mrrockka.repo.person.PersonColumnNames.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Component
public class PersonEntityRowMapper implements RowMapper<PersonEntity> {
  @Override
  public PersonEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    return mapRow(EMPTY, rs, rowNum);
  }

  public PersonEntity mapRow(final String prefix, final ResultSet rs, final int rowNum) throws SQLException {
    return PersonEntity.personBuilder()
      .id(UUID.fromString(rs.getString(prefix + ID)))
      .firstname(rs.getString(prefix + FIRST_NAME))
      .lastname(rs.getString(prefix + LAST_NAME))
      .nickname(rs.getString(prefix + NICK_NAME))
      .build();
  }
}


