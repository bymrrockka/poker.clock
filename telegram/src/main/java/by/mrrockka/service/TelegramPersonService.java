package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.person.PersonMessageMapper;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import by.mrrockka.repo.person.TelegramPersonRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TelegramPersonService {

  private final PersonMessageMapper personMessageMapper;
  private final TelegramPersonMapper telegramPersonMapper;
  private final PersonService personService;
  private final TelegramPersonRepository telegramPersonRepository;

  @Transactional(propagation = Propagation.REQUIRED)
  public List<UUID> storePersons(Update update) {
    final var command = update.getMessage().getText()
      .replaceFirst("@me(\b|$)", "@" + update.getMessage().getFrom().getUserName());
    final var chatId = update.getMessage().getChatId();
    final var persons = personMessageMapper.map(command, chatId);

    return saveAndMergedWhenRequired(persons, chatId);
  }

  // todo: refactor
  private List<UUID> saveAndMergedWhenRequired(List<TelegramPerson> persons, Long chatId) {
    final var stored = telegramPersonRepository.findByChatIdAndTelegrams(chatId, persons.stream().map(
      TelegramPerson::telegram).toList());

    final var storedTelegrams = stored.stream()
      .map(Pair::getValue)
      .toList();

    final var toStore = persons.stream()
      .filter(person -> !storedTelegrams.contains(person.telegram()))
      .toList();

    if (!toStore.isEmpty()) {
      personService.storeAll(telegramPersonMapper.map(toStore));
      telegramPersonRepository.saveAll(toStore);

      return Stream.concat(stored.stream().map(Pair::getKey), toStore.stream().map(Person::getId))
        .toList();
    }

    return persons.stream()
      .map(Person::getId)
      .toList();
  }

}
