package by.mrrockka.service.statistics;


import by.mrrockka.config.TelegramPSQLExtension;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
public class GameStatisticsTelegramServiceITest {

  private static final Long CHAT_ID = 123L;
  private static final Integer TOURNAMENT_GAME_REPLY_TO_ID = 3;
  private static final Integer CASH_GAME_REPLY_TO_ID = 4;
  private static final Integer BOUNTY_GAME_REPLY_TO_ID = 6;

  @Autowired
  private GameStatisticsTelegramService gameStatisticsTelegramService;

  private static Stream<Arguments> gameArguments() {
    return Stream.of(
      Arguments.of(
        TOURNAMENT_GAME_REPLY_TO_ID,
        """
          Game statistics:
          	- players entered -> 4
          	- number of entries -> 4
          	- total buy-in amount -> 120
          	"""
      ),
      Arguments.of(
        BOUNTY_GAME_REPLY_TO_ID,
        """
          Game statistics:
          	- players entered -> 4
          	- number of entries -> 5
          	- total buy-in amount -> 300
          	- bounties out of game -> 4
          	"""
      ),
      Arguments.of(
        CASH_GAME_REPLY_TO_ID,
        """
          Game statistics:
          	- players entered -> 4
          	- total buy-in amount -> 150
          	- total withdrawal amount -> 150
          """
      )
    );
  }

  @ParameterizedTest
  @MethodSource("gameArguments")
  void givenGame_whenGameStatisticsCommandPosted_shouldReturnMessageWithGameStatistics(final Integer gameId,
                                                                                       final String expected) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder
      .text("/game_statistics")
      .chatId(CHAT_ID)
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(gameId)))
    );

    final var statisticsCommand = StatisticsCommand.builder()
      .type(StatisticsType.GAME)
      .metadata(metadata)
      .build();

    final var response = (SendMessage) gameStatisticsTelegramService.retrieveStatistics(statisticsCommand);

    assertAll(
      () -> Assertions.assertThat(response).isNotNull(),
      () -> Assertions.assertThat(response.getText()).isEqualTo(expected),
      () -> Assertions.assertThat(response.getReplyToMessageId()).isEqualTo(gameId)
    );
  }

}
