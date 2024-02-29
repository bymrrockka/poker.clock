package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.CHAT_ID;
import static by.mrrockka.repo.person.TelegramPersonColumnNames.TELEGRAM;

@Component
@RequiredArgsConstructor
public class TelegramPersonEntityRowMapper implements RowMapper<TelegramPersonEntity> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public TelegramPersonEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    final var personEntity = personEntityRowMapper.mapRow(rs, rowNum);

    return TelegramPersonEntity.builder()
      .id(personEntity.getId())
      .firstname(personEntity.getFirstname())
      .lastname(personEntity.getLastname())
      .telegram(rs.getString(TELEGRAM))
      .chatId(rs.getLong(CHAT_ID))
      .build();
  }

}


