package by.mrrockka.service.statistics;

import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.PlayerInGameStatisticsResponseBuilder;
import by.mrrockka.service.GameTelegramService;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class PlayerInGameStatisticsTelegramService {

  private final PlayerInGameStatisticsResponseBuilder playerInGameStatisticsResponseBuilder;
  private final GameTelegramService gameTelegramService;
  private final PlayerInGameStatisticsService playerInGameStatisticsService;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    final var messageMetadata = statisticsCommand.metadata();
    final var telegramGame = gameTelegramService
      .findGame(messageMetadata);
    final var game = telegramGame.getGame();
    final var nickname = Optional.ofNullable(statisticsCommand.metadata().getFromNickname())
      .orElseThrow(PlayerHasNoNicknameException::new);

    final var playerInGameStatistics = playerInGameStatisticsService.retrieveStatistics(game, nickname);

    return SendMessage.builder()
      .chatId(messageMetadata.getChatId())
      .text(playerInGameStatisticsResponseBuilder.response(playerInGameStatistics))
      .replyToMessageId((int) telegramGame.getMessageMetadata().getId())
      .build();
  }

}
