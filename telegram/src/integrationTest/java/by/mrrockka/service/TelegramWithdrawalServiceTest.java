package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.creator.UserCreator;
import by.mrrockka.repo.withdrawals.WithdrawalsEntity;
import by.mrrockka.repo.withdrawals.WithdrawalsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramWithdrawalServiceTest {

  private static final UUID GAME_ID = UUID.fromString("51d973b6-cde3-4bbb-b67b-7555243dbc15");
  private static final Integer REPLY_TO_ID = 5;
  private static final Long CHAT_ID = 123L;

  @Autowired
  private TelegramWithdrawalService telegramWithdrawalService;
  @Autowired
  private WithdrawalsRepository withdrawalsRepository;

  private static Stream<Arguments> withdrawalsMessage() {
    return Stream.of(
      Arguments.of("/withdrawal @kinger 60", "kinger", BigDecimal.valueOf(60)),
      Arguments.of("/withdrawal @queen 15", "queen", BigDecimal.valueOf(15)),
      Arguments.of("/withdrawal @me 20", "jackas", BigDecimal.valueOf(20))
    );
  }

  @ParameterizedTest
  @MethodSource("withdrawalsMessage")
  void givenGameAndPerson_whenWithdrawalAttempt_shouldStoreWithdrawal(final String text, final String telegram,
                                                                      final BigDecimal expectedAmount) {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setText(text);
        message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(REPLY_TO_ID)));
        message.setFrom(UserCreator.user(telegram));
      })
    );

    final var response = (SendMessage) telegramWithdrawalService.storeWithdrawal(update);
    assertAll(
      () -> Assertions.assertThat(response).isNotNull(),
      () -> Assertions.assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> Assertions.assertThat(response.getText()).isEqualTo(
        "%s withdrawn %s amount.".formatted(telegram, expectedAmount))
    );

    final var actual = withdrawalsRepository.findAllByGameId(GAME_ID);
    assertAll(
      () -> Assertions.assertThat(actual).isNotEmpty(),
      () -> Assertions.assertThat(findByTelegram(actual, telegram).amounts()).hasSize(1),
      () -> Assertions.assertThat(findByTelegram(actual, telegram).amounts().get(0)).isEqualTo(expectedAmount)
    );
  }

  private WithdrawalsEntity findByTelegram(final List<WithdrawalsEntity> withdrawals, final String telegram) {
    return withdrawals.stream()
      .filter(withdr -> withdr.person().getNickname().equals(telegram))
      .findFirst()
      .orElseThrow();
  }

}