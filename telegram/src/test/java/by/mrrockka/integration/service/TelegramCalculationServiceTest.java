package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.service.TelegramCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramCalculationServiceTest {

  private static final Long CHAT_ID = 123L;
  private static final Integer TOURNAMENT_REPLY_TO_ID = 3;
  private static final Integer CASH_REPLY_TO_ID = 4;

  @Autowired
  private TelegramCalculationService telegramCalculationService;

  @Test
  void givenTournament_whenCalculateAttempt_shouldCalculateAndPrettyPrintData() {
    final var update = UpdateCreator.update(
      MessageCreator.message(
        message -> {
          message.setText("/calculate");
          message.setChat(ChatCreator.chat(CHAT_ID));
          message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(TOURNAMENT_REPLY_TO_ID)));
        }));

    final var expected = """
      -----------------------------
      Payout to: @kinger
      	Entries: 30
      	Prize: 84
      	Total: 54
      From
      	@tenten -> 30
      	@jackas -> 24

      -----------------------------
      Payout to: @queen
      	Entries: 30
      	Prize: 36
      	Total: 6
      From
      	@jackas -> 6
      	""";
    final var response = (SendMessage) telegramCalculationService.calculatePayments(update);

    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getText()).isEqualTo(expected),
      () -> assertThat(response.getReplyToMessageId()).isEqualTo(TOURNAMENT_REPLY_TO_ID)
    );
  }

  @Test
  void givenCash_whenCalculateAttempt_shouldCalculateAndPrettyPrintData() {
    final var update = UpdateCreator.update(
      MessageCreator.message(
        message -> {
          message.setText("/calculate");
          message.setChat(ChatCreator.chat(CHAT_ID));
          message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(CASH_REPLY_TO_ID)));
        }));

    final var expected = """
      -----------------------------
      Payout to: @tenten
      	Entries: 30
      	Withdrawals: 45
      	Total: 15
      From
      	@jackas -> 15
            
      -----------------------------
      Payout to: @kinger
      	Entries: 30
      	Withdrawals: 35
      	Total: 5
      From
      	@queen -> 5
      	""";
    final var response = (SendMessage) telegramCalculationService.calculatePayments(update);

    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getText()).isEqualTo(expected),
      () -> assertThat(response.getReplyToMessageId()).isEqualTo(CASH_REPLY_TO_ID)
    );
  }

}