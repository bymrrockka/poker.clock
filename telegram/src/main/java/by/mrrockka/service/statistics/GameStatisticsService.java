package by.mrrockka.service.statistics;

import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.GameStatisticsResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
class GameStatisticsService {

  private final GameStatisticsResponseBuilder gameStatisticsResponseBuilder;
  private final TelegramGameService telegramGameService;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(statisticsCommand.metadata())
      .orElseThrow(ChatGameNotFoundException::new);

    return SendMessage.builder()
      .chatId(statisticsCommand.metadata().chatId())
      .text(gameStatisticsResponseBuilder.response(telegramGame.game()))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
