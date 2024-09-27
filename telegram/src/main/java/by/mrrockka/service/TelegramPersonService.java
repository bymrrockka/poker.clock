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

import static java.util.Objects.nonNull;

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
    personMentionsValidator.validateMessageMentions(messageMetadata, 1);
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
//  todo: 1. change persons argument to messagemetadata
  public List<TelegramPerson> storeMissed(final List<TelegramPerson> persons, final Long chatId) {
//    todo: call person repo to get person by nickname and store all using chat id

//    todo: 2. select existing persons with chat id as map where key=chatId, value=person
    final var stored = telegramPersonMapper
      .mapToTelegramPersons(telegramPersonRepository.findAllByChatIdAndNicknames(chatId, persons.stream().map(
        TelegramPerson::getNickname).toList()));

    final var storedTelegramPersons = stored.stream()
      .map(TelegramPerson::getNickname)
      .toList();
//    todo: 3. get all the persons by null chatId and store them with this chatId
    final var toStore = persons.stream()
      .filter(person -> !storedTelegramPersons.contains(person.getNickname()))
      .toList();

    if (!toStore.isEmpty()) {
      personService.storeAll(telegramPersonMapper.mapToPersons(toStore));
      telegramPersonRepository.saveAll(toStore);

      return Stream.concat(stored.stream(), toStore.stream()).toList();
    }
//    todo: 4. map persons with chatId and return list

    return stored;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public List<TelegramPerson> storeMissed(final MessageMetadata metadata) {
    final var nicknames = metadata.mentions().map(entity -> entity.text().replaceAll("@", "")).toList();
    final var chatIdToPersonMap = telegramPersonRepository.findNewPersonIds(nicknames, metadata.chatId());

    /*
     * todo:
     *  - get not existent nick_names
     *  - save not existent persons
     *  - get absent ids in chat by nick_names
     *  - save not existent in chat persons
     * */


    final var existingPersons = chatIdToPersonMap.values().stream().flatMap(List::stream).toList();
    var toStore = nicknames.stream()
      .filter(nickname -> !existingPersons.contains(nickname))
      .toList();
    ;
    if (nicknames.size() != existingPersons.size()) {

    }

    final var toUpdate = chatIdToPersonMap.get(0);
//    todo: 3. get all the persons by null chatId and store them with this chatId
    if (nonNull(toUpdate) && !toUpdate.isEmpty()) {
//      personService.storeAll(toUpdate);
      telegramPersonRepository.saveAll(telegramPersonMapper.mapToTelegramPersons(metadata.chatId(), toUpdate));
    }
//    todo: 4. map persons with chatId and return list

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
