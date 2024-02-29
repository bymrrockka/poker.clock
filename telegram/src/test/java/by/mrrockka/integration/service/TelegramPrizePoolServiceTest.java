package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.service.GameService;
import by.mrrockka.service.PrizePoolService;
import by.mrrockka.service.TelegramPrizePoolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class TelegramPrizePoolServiceTest {

  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Instant GAME_TIMESTAMP = Instant.parse("2024-01-01T00:00:01Z");
  private static final Long CHAT_ID = 123L;

  private static final String PRIZE_POOL_COMMAND =
    """
      /prizepool
      1 60%, 2. 30%,3 - 10%
      """;

  @Autowired
  private TelegramPrizePoolService telegramPrizePoolService;
  @Autowired
  private GameService gameService;
  @Autowired
  private PrizePoolService prizePoolService;

  @Test
  void givenGameIdAndChatId_whenPrizePoolMessageReceived_shouldStorePrizePoolAgainstGame() {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setText(PRIZE_POOL_COMMAND);
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setPinnedMessage(MessageCreator.message(msg -> msg.setDate((int) GAME_TIMESTAMP.getEpochSecond())));
      })
    );

    final var message = telegramPrizePoolService.storePrizePool(update);
    assertAll(
      () -> assertThat(((SendMessage) message).getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(((SendMessage) message).getText()).isEqualTo("Prize pool for game %s stored.".formatted(GAME_ID))
    );

    final var expected = PrizePoolCreator.domain();
    final var actual = prizePoolService.getByGameId(GAME_ID);
    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual).isEqualTo(expected)
    );
  }
}