package by.mrrockka.service.statistics;

import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.GlobalPersonStatisticsResponseBuilder;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
class GlobalPersonStatisticsTelegramService {

  private final GlobalPersonStatisticsResponseBuilder globalPersonStatisticsResponseBuilder;
  private final GlobalPersonStatisticsService globalPersonStatisticsService;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    final var nickname = statisticsCommand.metadata().optFromNickname()
      .orElseThrow(PlayerHasNoNicknameException::new);

    final var globalStatistics = globalPersonStatisticsService.retrieveStatistics(nickname);

    return SendMessage.builder()
      .chatId(statisticsCommand.metadata().getChatId())
      .text(globalPersonStatisticsResponseBuilder.response(globalStatistics))
      .build();
  }

}
