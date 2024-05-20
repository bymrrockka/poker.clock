package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.person.PersonMessageMapper;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import by.mrrockka.repo.person.TelegramPersonRepository;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
  private final PersonMentionsValidator personMentionsValidator;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storePersons(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageMentions(messageMetadata, 2);
    final var persons = personMessageMapper.map(messageMetadata);

    return storeMissed(persons, messageMetadata.chatId());
  }

  public TelegramPerson getByNicknameAndChatId(final String nickname, final Long chatId) {
    return telegramPersonRepository.findByNickname(chatId, nickname)
      .map(telegramPersonMapper::mapToTelegramPerson)
      .orElseGet(() -> saveNew(nickname, chatId));
  }

  public List<TelegramPerson> getAllByNicknamesAndChatId(final List<String> telegrams, final Long chatId) {
    return telegramPersonMapper.mapToTelegramPersons(
      telegramPersonRepository.findAllByChatIdAndNicknames(chatId, telegrams));
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storeMissed(final List<TelegramPerson> persons, final Long chatId) {
//    todo: call person repo to get person by nickname and store all using chat id
    final var stored = telegramPersonMapper
      .mapToTelegramPersons(telegramPersonRepository.findAllByChatIdAndNicknames(chatId, persons.stream().map(
        TelegramPerson::getNickname).toList()));

    final var storedTelegramPersons = stored.stream()
      .map(TelegramPerson::getNickname)
      .toList();

    final var toStore = persons.stream()
      .filter(person -> !storedTelegramPersons.contains(person.getNickname()))
      .toList();

    if (!toStore.isEmpty()) {
      personService.storeAll(telegramPersonMapper.mapToPersons(toStore));
      telegramPersonRepository.saveAll(toStore);

      return Stream.concat(stored.stream(), toStore.stream()).toList();
    }

    return stored;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  private TelegramPerson saveNew(final String nickname, final Long chatId) {
    final var telegramPerson = TelegramPerson.telegramPersonBuilder()
      .id(UUID.randomUUID())
      .nickname(nickname)
      .chatId(chatId)
      .build();

    personService.store(telegramPerson);
    telegramPersonRepository.save(telegramPerson);
    return telegramPerson;
  }

}
