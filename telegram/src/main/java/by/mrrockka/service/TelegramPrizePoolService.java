package by.mrrockka.service;

import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.PrizePoolMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class TelegramPrizePoolService {

  private final PrizePoolService prizePoolService;
  private final PrizePoolMessageMapper prizePoolMessageMapper;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage storePrizePool(Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var prizePool = prizePoolMessageMapper.map(messageMetadata.command());

    if (prizePool.positionAndPercentages().isEmpty()) {
      throw new RuntimeException("No position and percentage list.");
    }

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(); //todo: add meaningful exception

//    todo: add pinned message referring to game
    prizePoolService.store(telegramGame.game().getId(), prizePool);
    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(prizePool.toString())
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
