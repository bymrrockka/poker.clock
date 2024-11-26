package by.mrrockka.service;

import by.mrrockka.config.TelegramPSQLExtension;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.PrizePoolCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class PrizePoolTelegramServiceITest {

  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Long CHAT_ID = 123L;
  private static final Integer REPLY_TO_ID = 1;
  private static final String PRIZE_POOL_MESSAGE =
    """
      /prizepool
      1 60%, 2. 30%,3 - 10%
      """;

  @Autowired
  private PrizePoolTelegramService prizePoolTelegramService;
  @Autowired
  private PrizePoolService prizePoolService;

  @Test
  void givenGameIdAndChatId_whenPrizePoolMessageReceived_shouldStorePrizePoolAgainstGame() {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(PRIZE_POOL_MESSAGE)
      .replyTo(MessageMetadataCreator.domain(reply -> reply.id(REPLY_TO_ID)))
    );

    final var message = prizePoolTelegramService.storePrizePool(messageMetadata);
    final var expectedMessage = """
      Prize Pool:
      	position: 1, percentage: 60
      	position: 2, percentage: 30
      	position: 3, percentage: 10
      	""";
    assertAll(
      () -> Assertions.assertThat(((SendMessage) message).getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> Assertions.assertThat(((SendMessage) message).getText()).isEqualTo(expectedMessage),
      () -> Assertions.assertThat(((SendMessage) message).getReplyToMessageId()).isEqualTo(REPLY_TO_ID)
    );

    final var expected = PrizePoolCreator.domain();
    final var actual = prizePoolService.getByGameId(GAME_ID);
    assertAll(
      () -> Assertions.assertThat(actual).isNotNull(),
      () -> Assertions.assertThat(actual).isEqualTo(expected)
    );
  }
}