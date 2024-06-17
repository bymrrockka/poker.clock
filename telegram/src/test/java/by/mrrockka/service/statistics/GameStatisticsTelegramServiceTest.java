package by.mrrockka.service.statistics;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.response.builder.GameStatisticsResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameStatisticsTelegramServiceTest {

  @Mock
  private GameStatisticsResponseBuilder gameStatisticsResponseBuilder;
  @Mock
  private GameTelegramFacadeService gameTelegramFacadeService;
  @InjectMocks
  private GameStatisticsTelegramService gameStatisticsTelegramService;

  @Test
  void givenStatisticsCommand_whenRetrieveStatisticsInvoked_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var telegramGame =
      TelegramGame.builder()
        .game(GameCreator.tournament())
        .messageMetadata(MessageMetadataCreator.domain())
        .build();

    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.GAME)
      .build();

    final var expectedMessage = "statistics";

    when(gameTelegramFacadeService.getGameByMessageMetadata(metadata)).thenReturn(Optional.of(telegramGame));
    when(gameStatisticsResponseBuilder.response(telegramGame.game())).thenReturn(expectedMessage);

    final var expected = SendMessage.builder()
      .chatId(metadata.chatId())
      .text(expectedMessage)
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();

    assertThat(gameStatisticsTelegramService.retrieveStatistics(statsCommand)).isEqualTo(expected);
  }

  @Test
  void givenMessageMetadata_whenNoAssociatedGameWithChatId_thenShouldTjrowException() {
    final var metadata = MessageMetadataCreator.domain();
    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.GAME)
      .build();

    when(gameTelegramFacadeService.getGameByMessageMetadata(metadata)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> gameStatisticsTelegramService.retrieveStatistics(statsCommand))
      .isInstanceOf(ChatGameNotFoundException.class);
  }


}