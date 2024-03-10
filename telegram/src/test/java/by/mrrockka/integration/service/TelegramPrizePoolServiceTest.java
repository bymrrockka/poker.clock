package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.service.PrizePoolService;
import by.mrrockka.service.TelegramPrizePoolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramPrizePoolServiceTest {

  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Long CHAT_ID = 123L;
  private static final Integer REPLY_TO_ID = 1;

  private static final String PRIZE_POOL_COMMAND =
    """
      /prizepool
      1 60%, 2. 30%,3 - 10%
      """;

  @Autowired
  private TelegramPrizePoolService telegramPrizePoolService;
  @Autowired
  private PrizePoolService prizePoolService;

  @Test
  void givenGameIdAndChatId_whenPrizePoolMessageReceived_shouldStorePrizePoolAgainstGame() {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setText(PRIZE_POOL_COMMAND);
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(REPLY_TO_ID)));
      })
    );

    final var message = telegramPrizePoolService.storePrizePool(update);
    final var expectedMessage = """
      Prize Pool:
      	position: 1, percentage: 60
      	position: 2, percentage: 30
      	position: 3, percentage: 10
      	""";
    assertAll(
      () -> assertThat(((SendMessage) message).getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(((SendMessage) message).getText()).isEqualTo(expectedMessage),
      () -> assertThat(((SendMessage) message).getReplyToMessageId()).isEqualTo(REPLY_TO_ID)
    );

    final var expected = PrizePoolCreator.domain();
    final var actual = prizePoolService.getByGameId(GAME_ID);
    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual).isEqualTo(expected)
    );
  }
}