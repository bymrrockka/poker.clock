package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.EntryMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.response.builder.EntryResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.TelegramGameService;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class TelegramEntryService {

  private final EntriesService entriesService;
  private final EntryMessageMapper entryMessageMapper;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final PersonMentionsValidator personMentionsValidator;
  private final EntryResponseBuilder entryResponseBuilder;

  @SneakyThrows
  public BotApiMethodMessage storeEntry(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    personMentionsValidator.validateMessageHasMentionsLessThen(messageMetadata, 1);
    personMentionsValidator.validateMessageHasUserTextMention(messageMetadata);
    final var nicknameAndAmountMap = entryMessageMapper.map(messageMetadata);
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    final var game = telegramGame.game();
    final var entries = nicknameAndAmountMap.keySet().stream().toList();
    final var amount = nicknameAndAmountMap.get(entries.get(0)).orElse(game.getBuyIn());

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
