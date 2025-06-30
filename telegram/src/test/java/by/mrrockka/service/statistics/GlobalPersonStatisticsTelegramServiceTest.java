package by.mrrockka.service.statistics;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.statistics.GlobalPersonStatistics;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.response.builder.GlobalPersonStatisticsResponseBuilder;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalPersonStatisticsTelegramServiceTest {

  private static final BigDecimal AMOUNT = BigDecimal.ONE;
  private static final Integer TIMES = 1;
  @Mock
  private GlobalPersonStatisticsResponseBuilder globalPersonStatisticsResponseBuilder;
  @Mock
  private GlobalPersonStatisticsService globalPersonStatisticsService;
  @InjectMocks
  private GlobalPersonStatisticsTelegramService globalPersonStatisticsTelegramService;

  @Test
  void givenStatisticsCommand_whenRetrieveStatisticsInvoked_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.PERSON_GLOBAL)
      .build();
    final var globalStatistics = GlobalPersonStatistics.builder()
      .person(PersonCreator.domainRandom())
      .outToInRatio(AMOUNT)
      .inPrizeRatio(AMOUNT)
      .totalMoneyWon(AMOUNT)
      .totalMoneyIn(AMOUNT)
      .totalMoneyOut(AMOUNT)
      .totalMoneyLose(AMOUNT)
      .timesOnFirstPlace(TIMES)
      .gamesPlayed(TIMES)
      .timesInPrizes(TIMES)
      .build();
    final var expectedMessage = "statistics";

    when(globalPersonStatisticsService.retrieveStatistics(metadata.getFromNickname())).thenReturn(globalStatistics);
    when(globalPersonStatisticsResponseBuilder.response(globalStatistics)).thenReturn(expectedMessage);

    final var expected = SendMessage.builder()
      .chatId(metadata.getChatId())
      .text(expectedMessage)
      .build();

    assertThat(globalPersonStatisticsTelegramService.retrieveStatistics(statsCommand)).isEqualTo(expected);
  }

  @Test
  void givenMessageMetadata_whenMessageHasNoFromUsername_thenShouldTjrowException() {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.fromNickname(null));
    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.PERSON_GLOBAL)
      .build();

    assertThatThrownBy(() -> globalPersonStatisticsTelegramService.retrieveStatistics(statsCommand))
      .isInstanceOf(PlayerHasNoNicknameException.class);
  }

}
