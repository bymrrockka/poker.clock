package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.TelegramPersonMapper;
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

  private final TelegramPersonMapper telegramPersonMapper;
  private final PersonService personService;
  private final TelegramPersonRepository telegramPersonRepository;
  private final PersonMentionsValidator personMentionsValidator;

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storePersons(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageMentions(messageMetadata, 1);
    return storeMissed(messageMetadata);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public TelegramPerson getByNicknameAndChatId(final String nickname, final Long chatId) {
    return telegramPersonRepository.findByNickname(chatId, nickname)
      .map(telegramPersonMapper::mapToTelegramPerson)
      .orElseGet(() -> saveNew(nickname, chatId));
  }

  public List<TelegramPerson> getAllByNicknamesAndChatId(final List<String> telegrams, final Long chatId) {
    return telegramPersonMapper.mapToTelegramPersons(
      telegramPersonRepository.findAllByChatIdAndNicknames(telegrams, chatId));
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storeMissed(final MessageMetadata metadata) {
    final var nicknames = metadata.mentions().map(entity -> entity.text().replaceAll("@", "")).toList();
    final var newNicknames = personService.getNewNicknames(nicknames);
    final var notExistentInChat = telegramPersonRepository.findNotExistentInChat(nicknames, metadata.chatId());

    final var newPersons = newNicknames.stream()
      .map(nickname -> Person.personBuilder()
        .nickname(nickname)
        .id(UUID.randomUUID())
        .build())
      .toList();

    if (!newPersons.isEmpty()) {
      personService.storeAll(newPersons);
    }

    if (!notExistentInChat.isEmpty() || !newPersons.isEmpty()) {
      final var allNewPersonIds = Stream.of(
          newPersons.stream().map(Person::getId).toList(),
          notExistentInChat
        ).flatMap(List::stream)
        .toList();

      telegramPersonRepository.saveAll(allNewPersonIds, metadata.chatId());
    }

    return telegramPersonMapper.mapToTelegramPersons(
      telegramPersonRepository.findAllByChatIdAndNicknames(nicknames, metadata.chatId()));
  }

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
