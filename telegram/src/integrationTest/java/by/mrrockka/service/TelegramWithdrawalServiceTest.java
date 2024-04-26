package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.*;
import by.mrrockka.repo.withdrawals.WithdrawalsEntity;
import by.mrrockka.repo.withdrawals.WithdrawalsRepository;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
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
        if (!text.contains("@me")) {
          message.setEntities(List.of(MessageEntityCreator.apiMention(text, "@%s".formatted(telegram))));
        }
      })
    );

    final var response = (SendMessage) telegramWithdrawalService.storeWithdrawal(update);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(
        "Withdrawals:\n - @%s -> %s".formatted(telegram, expectedAmount))
    );

    final var actual = withdrawalsRepository.findAllByGameId(GAME_ID);
    assertAll(
      () -> assertThat(actual).isNotEmpty(),
      () -> assertThat(findByTelegram(actual, telegram).amounts()).hasSize(1),
      () -> assertThat(findByTelegram(actual, telegram).amounts().get(0)).isEqualTo(expectedAmount)
    );
  }


  private static Stream<Arguments> multipleWithdrawalsMessage() {
    return Stream.of(
      Arguments.of("/withdrawal @mister @missis 60", List.of("mister", "missis"), BigDecimal.valueOf(60)),
      Arguments.of("/withdrawal @smith @candle 15", List.of("smith", "candle"), BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("multipleWithdrawalsMessage")
  void givenGameAndPersons_whenMultipleWithdrawalsAttempt_shouldStoreWithdrawal(final String text,
                                                                                final List<String> telegrams,
                                                                                final BigDecimal expectedAmount) {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setText(text);
        message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(REPLY_TO_ID)));
        message.setEntities(telegrams.stream()
                              .map(tg -> MessageEntityCreator.apiMention(text, "@%s".formatted(tg)))
                              .toList());
      })
    );

    final var expectedLines = telegrams.stream()
      .map(telegram -> " - @%s -> %s".formatted(telegram, expectedAmount))
      .collect(Collectors.toSet());
    expectedLines.add("Withdrawals:\n");

    final var response = (SendMessage) telegramWithdrawalService.storeWithdrawal(update);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).contains(expectedLines)
    );

    final var entries = withdrawalsRepository.findAllByGameId(GAME_ID);
    assertThat(entries).isNotEmpty();
    telegrams.forEach(telegram -> {
      final var actual = entries.stream()
        .filter(entry -> entry.person().getNickname().equals(telegram))
        .findAny();

      assertAll(
        () -> assertThat(actual).isNotEmpty(),
        () -> assertThat(actual.get().amounts()).hasSize(1),
        () -> assertThat(actual.get().amounts().get(0)).isEqualTo(expectedAmount)
      );
    });
  }

  private WithdrawalsEntity findByTelegram(final List<WithdrawalsEntity> withdrawals, final String telegram) {
    return withdrawals.stream()
      .filter(withdr -> withdr.person().getNickname().equals(telegram))
      .findFirst()
      .orElseThrow();
  }

}