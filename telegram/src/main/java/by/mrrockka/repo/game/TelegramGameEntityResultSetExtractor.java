package by.mrrockka.repo.game;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static by.mrrockka.repo.game.TelegramGameColumnNames.*;

@Component
public class TelegramGameEntityResultSetExtractor implements ResultSetExtractor<Optional<TelegramGameEntity>> {
  @Override
  public Optional<TelegramGameEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    if (rs.next()) {
      return Optional.of(
        TelegramGameEntity.builder()
          .gameId(rs.getObject(GAME_ID, UUID.class))
          .messageId(rs.getInt(MESSAGE_ID))
          .createdAt(rs.getTimestamp(CREATED_AT).toInstant())
          .chatId(rs.getLong(CHAT_ID))
          .build()
      );
    }
    return Optional.empty();
  }
}
