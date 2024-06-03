package by.mrrockka.service.statistics;

import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.PlayerInGameStatisticsResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
class PlayerInGameStatisticsTelegramService {

  private final PlayerInGameStatisticsResponseBuilder playerInGameStatisticsResponseBuilder;
  private final GameTelegramFacadeService gameTelegramFacadeService;
  private final PlayerInGameStatisticsService playerInGameStatisticsService;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    final var messageMetadata = statisticsCommand.metadata();
    final var telegramGame = gameTelegramFacadeService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    final var game = telegramGame.game();

    final var nickname = statisticsCommand.metadata().optFromNickname()
      .orElseThrow(PlayerHasNoNicknameException::new);

    final var playerInGameDetails = playerInGameStatisticsService.retrieveStatistics(game, nickname);

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(playerInGameStatisticsResponseBuilder.response(playerInGameDetails))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

}
