package by.mrrockka.service;

import by.mrrockka.mapper.EntryMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramEntryService {

  private final EntriesService entriesService;
  private final EntryMessageMapper entryMessageMapper;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final UsernameReplaceUtil usernameReplaceUtil;

  public BotApiMethodMessage storeEntry(Update update) {
    final var command = usernameReplaceUtil.replaceUsername(update);
    final var message = update.getMessage();
    final var chatId = message.getChatId();
    final var messageTimestamp = Instant.ofEpochSecond(message.getDate());
    final var pinnedMessageTimestamp = Optional.ofNullable(message.getPinnedMessage())
      .map(Message::getDate)
      .map(Instant::ofEpochSecond)
      .orElse(null);

    final var telegramAndAmount = entryMessageMapper.map(command);

    final var game = telegramGameService.getGameByTimestampOrLatest(chatId, pinnedMessageTimestamp)
      .orElseThrow(); //todo: add meaningful exception

    final var person = telegramPersonService.getByTelegramAndChatId(telegramAndAmount.getKey(), chatId);

    entriesService
      .storeEntry(game.getId(), person.getId(), telegramAndAmount.getValue().orElse(game.getBuyIn()), messageTimestamp);

    return SendMessage.builder()
      .chatId(chatId)
      .text("%s enters the game with %s amount"
              .formatted(telegramAndAmount.getKey(), telegramAndAmount.getValue().orElse(game.getBuyIn())))
      .build();
  }
}
