package by.mrrockka.service;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.person.PersonMessageMapper;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import by.mrrockka.repo.person.TelegramPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
  private final MessageMetadataMapper messageMetadataMapper;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storePersons(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var persons = personMessageMapper.map(messageMetadata.command(), messageMetadata.chatId());

    return storeMissed(persons, messageMetadata.chatId());
  }

  public TelegramPerson getByTelegramAndChatId(final String telegram, final Long chatId) {
    return telegramPersonRepository.findByTelegram(chatId, telegram)
      .map(telegramPersonMapper::mapToTelegram)
      .orElseGet(() -> saveNew(telegram, chatId));
  }

  public List<TelegramPerson> getAllByTelegramsAndChatId(final List<String> telegrams, final Long chatId) {
    return telegramPersonMapper.mapToTelegrams(telegramPersonRepository.findAllByChatIdAndTelegrams(chatId, telegrams));
  }

  private List<TelegramPerson> storeMissed(final List<TelegramPerson> persons, final Long chatId) {
    final var stored = telegramPersonMapper
      .mapToTelegrams(telegramPersonRepository.findAllByChatIdAndTelegrams(chatId, persons.stream().map(
        TelegramPerson::getNickname).toList()));

    final var storedTelegrams = stored.stream()
      .map(TelegramPerson::getNickname)
      .toList();

    final var toStore = persons.stream()
      .filter(person -> !storedTelegrams.contains(person.getNickname()))
      .toList();

    if (!toStore.isEmpty()) {
      personService.storeAll(telegramPersonMapper.mapToPersons(toStore));
      telegramPersonRepository.saveAll(toStore);

      return Stream.concat(stored.stream(), toStore.stream()).toList();
    }

    return stored;
  }

  private TelegramPerson saveNew(final String telegram, final Long chatId) {
    final var telegramPerson = TelegramPerson.telegramPersonBuilder()
      .id(UUID.randomUUID())
      .nickname(telegram)
      .chatId(chatId)
      .build();

    personService.store(telegramPerson);
    telegramPersonRepository.save(telegramPerson);
    return telegramPerson;
  }

}
