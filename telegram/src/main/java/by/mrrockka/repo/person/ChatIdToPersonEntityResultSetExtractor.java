package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.CHAT_ID;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ChatIdToPersonEntityResultSetExtractor implements ResultSetExtractor<Map<Long, List<PersonEntity>>> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public Map<Long, List<PersonEntity>> extractData(final ResultSet rs) throws SQLException, DataAccessException {
    final var chatToPerson = new HashMap<Long, List<PersonEntity>>();
    while (rs.next()) {
      final var chatId = rs.getLong(CHAT_ID);
      var value = chatToPerson.get(chatId);
      if (nonNull(value)) {
        value.add(personEntityRowMapper.mapRow(rs, rs.getRow()));
      } else {
        value = new ArrayList<>() {{
          this.add(personEntityRowMapper.mapRow(rs, rs.getRow()));
        }};
      }
      chatToPerson.put(chatId, value);
    }
    return chatToPerson;
  }
}


