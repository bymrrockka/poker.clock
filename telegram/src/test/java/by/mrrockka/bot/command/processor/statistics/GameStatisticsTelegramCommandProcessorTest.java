package by.mrrockka.bot.command.processor.statistics;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.SendMessageCreator;
import by.mrrockka.service.statistics.StatisticsTelegramFacadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameStatisticsTelegramCommandProcessorTest {
  @Mock
  private StatisticsTelegramFacadeService statisticsTelegramFacadeService;
  @InjectMocks
  private GameStatisticsTelegramCommandProcessor gameStatisticsTelegramCommandProcessor;

  @Test
  void givenMessageMetadata_whenProcessorExecuted_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var expected = SendMessageCreator.api();

    when(statisticsTelegramFacadeService.retrieveStatistics(metadata)).thenReturn(expected);
    assertThat(gameStatisticsTelegramCommandProcessor.process(metadata)).isEqualTo(expected);
  }
}