package by.mrrockka.service;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.person.PersonMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramPersonService {

  private final PersonMessageMapper personMessageMapper;

  public List<TelegramPerson> storePersons(Update update) {
    final var command = update.getMessage().getText()
      .replaceFirst("@me", "@" + update.getMessage().getFrom().getUserName());
    final var chatId = update.getMessage().getChatId();

//    todo: find all by telegrams and store the ones is not in the db

    final var persons = personMessageMapper.map(command, chatId);
    return persons;
  }
}
