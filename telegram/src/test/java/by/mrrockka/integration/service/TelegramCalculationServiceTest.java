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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class TelegramCalculationServiceTest {

  private static final Long CHAT_ID = 123L;

  @Autowired
  private TelegramCalculationService telegramCalculationService;

  @Test
  void givenGameWithPrizePoolAndFinalePlaces_whenCalculateAttempt_shouldCalculateAndPrettyPrintData() {
    final var update = UpdateCreator.update(
      MessageCreator.message(
        message -> {
          message.setText("/calculate");
          message.setChat(ChatCreator.chat(CHAT_ID));
        }));

    final var expected = """
      -----------------------------
      Payout to: @king
      From
      	@ten -> 30
      	@jack -> 24
            
      -----------------------------
      Payout to: @queen
      From
      	@jack -> 6
      """;
    final var response = (SendMessage) telegramCalculationService.calculatePayments(update);

    assertThat(response.getText())
      .isEqualTo(expected);
  }

}