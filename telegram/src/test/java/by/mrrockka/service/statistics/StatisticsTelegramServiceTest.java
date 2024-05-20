package by.mrrockka.service.statistics;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.mapper.StatisticsMessageMapper;
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
  private StatisticsMessageMapper statisticsMessageMapper;
  @Mock
  private PersonMentionsValidator personMentionsValidator;
  @Mock
  private PlayerInGameStatisticsService playerInGameStatisticsService;
  @Mock
  private GameStatisticsService gameStatisticsService;
  @InjectMocks
  private StatisticsTelegramService statisticsService;

  @Test
  void givenUpdateWithValidGameCommand_whenRetrieveStatisticsCalled_shouldReturnMessageWithGameResponse() {
    final var text = "/game_stats";
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));
    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.GAME)
      .metadata(metadata)
      .build();

    final var expected = SendMessage.builder()
      .chatId(metadata.chatId())
      .text("game response")
      .build();

    when(statisticsMessageMapper.map(metadata)).thenReturn(statisticsCommand);
    when(gameStatisticsService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verifyNoMoreInteractions(statisticsMessageMapper, gameStatisticsService);
    verify(personMentionsValidator, only()).validateMessageHasUserTextMention(metadata);
    verifyNoInteractions(playerInGameStatisticsService);
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
      .chatId(metadata.chatId())
      .text("player response")
      .build();

    when(statisticsMessageMapper.map(metadata)).thenReturn(statisticsCommand);
    when(playerInGameStatisticsService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verifyNoMoreInteractions(statisticsMessageMapper, playerInGameStatisticsService);
    verify(personMentionsValidator, only()).validateMessageHasUserTextMention(metadata);
    verifyNoInteractions(gameStatisticsService);
  }

  @Test
  void givenUpdateWithValidGlobalPersonCommand_whenRetrieveStatisticsCalled_shouldReturnMessageWithGlobalPersonResponse() {
    final var text = "/my_global_stats";
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));
    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.PERSON_GLOBAL)
      .metadata(metadata)
      .build();

    final var expected = SendMessage.builder()
      .chatId(metadata.chatId())
      .text("global person response")
      .build();

    when(statisticsMessageMapper.map(metadata)).thenReturn(statisticsCommand);
    when(playerInGameStatisticsService.retrieveStatistics(statisticsCommand)).thenReturn(expected);

    assertThat(statisticsService.retrieveStatistics(metadata)).isEqualTo(expected);
    verifyNoMoreInteractions(statisticsMessageMapper, playerInGameStatisticsService);
    verify(personMentionsValidator, only()).validateMessageHasUserTextMention(metadata);
    verifyNoInteractions(gameStatisticsService);
  }
}