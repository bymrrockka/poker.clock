package by.mrrockka.service.statistics;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.parser.StatisticsMessageParser;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsTelegramServiceTest {

  @Mock
  private StatisticsMessageParser statisticsMessageParser;
  @Mock
  private PersonMentionsValidator personMentionsValidator;
  @Mock
  private PlayerStatisticsTelegramService playerInGameStatisticsTelegramService;
  @Mock
  private GlobalPersonStatisticsTelegramService globalPersonStatisticsTelegramService;
  @Mock
  private GameStatisticsTelegramService gameStatisticsTelegramService;
  @InjectMocks
  private StatisticsTelegramFacadeService statisticsService;

  @Test
  void givenUpdateWithValidGameCommand_whenRetrieveStatisticsCalled_shouldReturnMessageWithGameResponse() {
    final var text = "/game_stats";
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));
    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.GAME)
      .metadata(metadata)
      .build();

    final var expected = SendMessage.builder()
      .chatId(metadata.getChatId())
      .text("game response")
      .build();

    when(statisticsMessageParser.map(metadata)).thenReturn(statisticsCommand);
    when(gameStatisticsTelegramService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verify(personMentionsValidator, only()).validateMessageHasNoUserTextMention(metadata);
    verifyNoInteractions(playerInGameStatisticsTelegramService, globalPersonStatisticsTelegramService);
    verifyNoMoreInteractions(statisticsMessageParser, gameStatisticsTelegramService);
  }

  @Test
  void givenUpdateWithValidPlayerInGameCommand_whenRetrieveStatisticsCalled_shouldReturnMessageWithPlayerResponse() {
    final var text = "/my_stats";
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));
    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.PLAYER_IN_GAME)
      .metadata(metadata)
      .build();

    final var expected = SendMessage.builder()
      .chatId(metadata.getChatId())
      .text("player response")
      .build();

    when(statisticsMessageParser.map(metadata)).thenReturn(statisticsCommand);
//    when(playerInGameStatisticsTelegramService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verify(personMentionsValidator, only()).validateMessageHasNoUserTextMention(metadata);
    verifyNoInteractions(gameStatisticsTelegramService, globalPersonStatisticsTelegramService);
    verifyNoMoreInteractions(statisticsMessageParser, playerInGameStatisticsTelegramService);
  }

  @Test
  void givenUpdateWithValidGlobalPersonCommand_whenRetrieveStatisticsCalled_shouldReturnMessageWithGlobalPersonResponse() {
    final var text = "/global_stats";
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));
    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.PERSON_GLOBAL)
      .metadata(metadata)
      .build();

    final var expected = SendMessage.builder()
      .chatId(metadata.getChatId())
      .text("global person response")
      .build();

    when(statisticsMessageParser.map(metadata)).thenReturn(statisticsCommand);
    when(globalPersonStatisticsTelegramService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verify(personMentionsValidator, only()).validateMessageHasNoUserTextMention(metadata);
    verifyNoInteractions(gameStatisticsTelegramService, playerInGameStatisticsTelegramService);
    verifyNoMoreInteractions(statisticsMessageParser, globalPersonStatisticsTelegramService);
  }
}