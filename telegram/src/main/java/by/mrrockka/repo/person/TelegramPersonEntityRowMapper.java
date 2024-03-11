package by.mrrockka.repo.person;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static by.mrrockka.repo.person.TelegramPersonColumnNames.CHAT_ID;

@Component
@RequiredArgsConstructor
public class TelegramPersonEntityRowMapper implements RowMapper<TelegramPersonEntity> {

  private final PersonEntityRowMapper personEntityRowMapper;

  @Override
  public TelegramPersonEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    final var personEntity = personEntityRowMapper.mapRow(rs, rowNum);

    return TelegramPersonEntity.telegramPersonBuilder()
      .id(personEntity.getId())
      .firstname(personEntity.getFirstname())
      .lastname(personEntity.getLastname())
      .nickname(personEntity.getNickname())
      .chatId(rs.getLong(CHAT_ID))
      .build();
  }

}


