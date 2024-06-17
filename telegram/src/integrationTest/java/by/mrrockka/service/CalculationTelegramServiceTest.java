package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageMetadataCreator;
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

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class CalculationTelegramServiceTest {

  private static final Long CHAT_ID = 123L;
  private static final Integer TOURNAMENT_GAME_REPLY_TO_ID = 3;
  private static final Integer CASH_GAME_REPLY_TO_ID = 4;
  private static final Integer BOUNTY_GAME_REPLY_TO_ID = 6;

  @Autowired
  private CalculationTelegramService calculationTelegramService;


  private static Stream<Arguments> gameArguments() {
    return Stream.of(
      Arguments.of(
        TOURNAMENT_GAME_REPLY_TO_ID,
        """
          -----------------------------
          Finale places:
          1. @kinger won 84
          2. @queen won 36
          Total: 120 (4 entries * 30 buy in)
          -----------------------------
          Payout to: @kinger
          	Entries: 1
          	Total: 54 (won 84 - entries 30)
          From
          	@tenten -> 30
          	@jackas -> 24
          -----------------------------
          Payout to: @queen
          	Entries: 1
          	Total: 6 (won 36 - entries 30)
          From
          	@jackas -> 6
          	"""
      ),
      Arguments.of(
        BOUNTY_GAME_REPLY_TO_ID,
        """
          -----------------------------
          Finale places:
          1. @tenten won 105
          2. @queen won 45
          Total: 150 (4 entries * 30 buy in)
          -----------------------------
          Payout to: @tenten
          	Entries: 1
          	Bounties: 60 (taken 2)
          	Total: 135 (won 105 - entries 30 + bounties 60)
          From
          	@jackas -> 120
          	@kinger -> 15
          -----------------------------
          Payout to: @queen
          	Entries: 1
          	Bounties: 0 (taken 1 - given 1)
          	Total: 15 (won 45 - entries 30 + bounties 0)
          From
          	@kinger -> 15
          """
      ),
      Arguments.of(
        CASH_GAME_REPLY_TO_ID,
        """
          -----------------------------
          Payout to: @tenten
          	Entries: 30
          	Withdrawals: 45
          	Total: 15 (withdrawal 45 - entries 30)
          From
          	@jackas -> 15
          -----------------------------
          Payout to: @kinger
          	Entries: 30
          	Withdrawals: 35
          	Total: 5 (withdrawal 35 - entries 30)
          From
          	@queen -> 5
          """
      )
    );
  }

  @ParameterizedTest
  @MethodSource("gameArguments")
  void givenGame_whenCalculateAttempt_shouldCalculateAndPrettyPrintData(final Integer gameId, final String expected) {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text("/calculate")
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(gameId)))
    );

    final var response = (SendMessage) calculationTelegramService.calculatePayouts(messageMetadata);

    assertAll(
      () -> Assertions.assertThat(response).isNotNull(),
      () -> Assertions.assertThat(response.getText()).isEqualTo(expected),
      () -> Assertions.assertThat(response.getReplyToMessageId()).isEqualTo(gameId)
    );
  }

}