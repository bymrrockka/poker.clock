package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
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
class WithdrawalTelegramServiceITest {

  private static final UUID GAME_ID = UUID.fromString("51d973b6-cde3-4bbb-b67b-7555243dbc15");
  private static final Integer REPLY_TO_ID = 5;
  private static final Long CHAT_ID = 123L;

  @Autowired
  private WithdrawalTelegramService withdrawalTelegramService;
  @Autowired
  private WithdrawalsRepository withdrawalsRepository;

  private static Stream<Arguments> withdrawalsMessage() {
    return Stream.of(
      Arguments.of("/withdrawal @kinger 60", "kinger", BigDecimal.valueOf(60)),
      Arguments.of("/withdrawal @queen 15", "queen", BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("withdrawalsMessage")
  void givenGameAndPerson_whenWithdrawalAttempt_shouldStoreWithdrawal(final String text, final String nickname,
                                                                      final BigDecimal expectedAmount) {
    final var messageMetadata = MessageMetadataCreator
      .domain(metadata -> metadata
        .chatId(CHAT_ID)
        .text(text)
        .fromNickname(nickname)
        .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
        .entities(List.of(MessageEntityCreator.domainMention("@%s".formatted(nickname))))
      );

    final var response = (SendMessage) withdrawalTelegramService.storeWithdrawal(messageMetadata);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(
        "Withdrawals:\n - @%s -> %s\n".formatted(nickname, expectedAmount))
    );

    final var actual = withdrawalsRepository.findAllByGameId(GAME_ID);
    assertAll(
      () -> assertThat(actual).isNotEmpty(),
      () -> assertThat(findByNickname(actual, nickname).amounts()).hasSize(1),
      () -> assertThat(findByNickname(actual, nickname).amounts().get(0)).isEqualTo(expectedAmount)
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
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(text)
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
      .entities(telegrams.stream()
                  .map(tg -> MessageEntityCreator.domainMention("@%s".formatted(tg)))
                  .toList())
    );

    final var expectedLines = telegrams.stream()
      .map(telegram -> " - @%s -> %s".formatted(telegram, expectedAmount))
      .collect(Collectors.toSet());
    expectedLines.add("Withdrawals:\n");

    final var response = (SendMessage) withdrawalTelegramService.storeWithdrawal(messageMetadata);
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

  private WithdrawalsEntity findByNickname(final List<WithdrawalsEntity> withdrawals, final String nickname) {
    return withdrawals.stream()
      .filter(withdrawal -> withdrawal.person().getNickname().equals(nickname))
      .findFirst()
      .orElseThrow();
  }

}