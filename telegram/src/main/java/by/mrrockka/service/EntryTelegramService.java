package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.mapper.EntryMessageMapper;
import by.mrrockka.response.builder.EntryResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramService;
import by.mrrockka.validation.collection.CollectionsValidator;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntryTelegramService {

  private final EntriesService entriesService;
  private final EntryMessageMapper entryMessageMapper;
  private final GameTelegramService gameTelegramService;
  private final TelegramPersonService telegramPersonService;
  private final PersonMentionsValidator personMentionsValidator;
  private final EntryResponseBuilder entryResponseBuilder;
  private final CollectionsValidator collectionsValidator;

  @SneakyThrows
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public BotApiMethodMessage storeEntry(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageMentions(messageMetadata, 1);
    final var personAndAmountMap = entryMessageMapper.map(messageMetadata);
    collectionsValidator.validateMapIsNotEmpty(personAndAmountMap, "Entry");

    final var telegramGame = gameTelegramService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    final var game = telegramGame.game();
    final var entries = personAndAmountMap.keySet().stream().toList();
    final var amount = personAndAmountMap.values().stream()
      .filter(Optional::isPresent)
      .map(Optional::get)
      .findFirst()
      .orElse(game.getBuyIn());

    final var persons = telegramPersonService.storeMissed(entries, messageMetadata.chatId());

    entriesService.storeBatch(game.getId(), persons.stream().map(Person::getId).toList(), amount,
                              messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(entryResponseBuilder.response(persons, amount))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
