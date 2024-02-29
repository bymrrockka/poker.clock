package by.mrrockka.service;

import by.mrrockka.domain.game.Game;
import by.mrrockka.mapper.PrizePoolMessageMapper;
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
public class TelegramPrizePoolService {

  private final PrizePoolService prizePoolService;
  private final PrizePoolMessageMapper prizePoolMessageMapper;
  private final TelegramGameService telegramGameService;

  public BotApiMethodMessage storePrizePool(Update update) {
    final var command = update.getMessage().getText();
    final var chatId = update.getMessage().getChatId();
    final var pinnedMessageTimestamp = Optional.ofNullable(update.getMessage().getPinnedMessage())
      .map(Message::getDate)
      .map(Instant::ofEpochSecond)
      .orElse(null);

    final var prizePool = prizePoolMessageMapper.map(command);

    final var gameId = telegramGameService.getGameByTimestampOrLatest(chatId, pinnedMessageTimestamp)
      .map(Game::getId)
      .orElseThrow(); //todo: add meaningful exception

    prizePoolService.store(gameId, prizePool);
    return SendMessage.builder()
      .chatId(chatId)
      .text("Prize pool for game %s stored.".formatted(gameId))
      .build();
  }
}
