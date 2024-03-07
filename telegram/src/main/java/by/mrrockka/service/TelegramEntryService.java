package by.mrrockka.service;

import by.mrrockka.mapper.EntryMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import lombok.RequiredArgsConstructor;
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

  public BotApiMethodMessage storeEntry(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var telegramAndAmount = entryMessageMapper.map(messageMetadata.command());
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(); //todo: add meaningful exception

    final var game = telegramGame.game();
    final var person = telegramPersonService.getByTelegramAndChatId(telegramAndAmount.getKey(),
                                                                    messageMetadata.chatId());

    entriesService.storeEntry(game.getId(), person.getId(), telegramAndAmount.getValue().orElse(game.getBuyIn()),
                              messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("%s enters the game. Entry amount is %s"
              .formatted(telegramAndAmount.getKey(), telegramAndAmount.getValue().orElse(game.getBuyIn())))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
