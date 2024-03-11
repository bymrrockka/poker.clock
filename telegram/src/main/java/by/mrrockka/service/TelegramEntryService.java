package by.mrrockka.service;

import by.mrrockka.mapper.EntryMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.TelegramGameService;
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
    final var nicknameAndAmount = entryMessageMapper.map(messageMetadata.command());
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    final var game = telegramGame.game();
    final var person = telegramPersonService.getByTelegramAndChatId(nicknameAndAmount.getKey(),
                                                                    messageMetadata.chatId());

    entriesService.storeEntry(game.getId(), person.getId(), nicknameAndAmount.getValue().orElse(game.getBuyIn()),
                              messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("%s enters the game with %s amount"
              .formatted(nicknameAndAmount.getKey(), nicknameAndAmount.getValue().orElse(game.getBuyIn())))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
