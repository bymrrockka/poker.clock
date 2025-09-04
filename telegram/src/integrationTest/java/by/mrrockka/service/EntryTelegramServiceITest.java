package by.mrrockka.service;

import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.extension.TelegramPSQLExtension;
import by.mrrockka.repo.entries.EntriesRepository;
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

@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class EntryTelegramServiceITest {

  private static final UUID GAME_ID = UUID.fromString("b759ac52-1496-463f-b0d8-982deeac085c");
  private static final Integer REPLY_TO_ID = 2;
  private static final Long CHAT_ID = 123L;

  @Autowired
  private EntryTelegramService entryTelegramService;
  @Autowired
  private TelegramPersonServiceOld telegramPersonServiceOld;
  @Autowired
  private EntriesRepository entriesRepository;

  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @kinger 60", "kinger", BigDecimal.valueOf(60)),
      Arguments.of("/entry @queen", "queen", BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenGameAndPerson_whenEntryAttempt_shouldStoreEntry(final String text, final String nickname,
                                                            final BigDecimal expectedAmount) {
    final var messageMetadata = MessageMetadataCreator
      .domain(metadata -> metadata
        .chatId(CHAT_ID)
        .text(text)
        .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
        .metadataEntities(List.of(MessageEntityCreator.domainMention("@%s".formatted(nickname))))
      );

    final var response = (SendMessage) entryTelegramService.storeEntry(messageMetadata);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(
        "Entries:\n - @%s -> %s\n".formatted(nickname, expectedAmount))
    );

    final var telegramPerson = telegramPersonServiceOld.getByNicknameAndChatId(nickname, CHAT_ID);
    final var actual = entriesRepository.findByGameAndPerson(GAME_ID, telegramPerson.getId());
    assertAll(
      () -> assertThat(actual).isNotEmpty(),
      () -> assertThat(actual.get().amounts()).hasSize(1),
      () -> assertThat(actual.get().amounts().get(0)).isEqualTo(expectedAmount)
    );
  }

  private static Stream<Arguments> multipleEntryMessage() {
    return Stream.of(
      Arguments.of("/entry @mister @missis 60", List.of("mister", "missis"), BigDecimal.valueOf(60)),
      Arguments.of("/entry @smith @candle", List.of("smith", "candle"), BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("multipleEntryMessage")
  void givenGameAndPersons_whenMultipleEntryAttempt_shouldStoreEntry(final String text, final List<String> telegrams,
                                                                     final BigDecimal expectedAmount) {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(text)
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
      .metadataEntities(telegrams.stream()
                  .map(tg -> MessageEntityCreator.domainMention("@%s".formatted(tg)))
                  .toList())
    );

    final var expectedLines = telegrams.stream()
      .map(telegram -> " - @%s -> %s".formatted(telegram, expectedAmount))
      .collect(Collectors.toSet());
    expectedLines.add("Entries:\n");

    final var response = (SendMessage) entryTelegramService.storeEntry(messageMetadata);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).contains(expectedLines)
    );

    final var telegramPersons = telegramPersonServiceOld.getAllByNicknamesAndChatId(telegrams, CHAT_ID).stream()
      .map(TelegramPerson::getNickname)
      .toList();

    assertAll(
      () -> assertThat(telegramPersons).isNotEmpty(),
      () -> assertThat(telegramPersons).containsAll(telegrams)
    );

    final var entries = entriesRepository.findAllByGameId(GAME_ID);
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

  private static Stream<Arguments> newPersonNicknamesMessage() {
    return Stream.of(
      Arguments.of("/entry @asdfasf 60", "asdfasf", BigDecimal.valueOf(60)),
      Arguments.of("/entry @omoekrngoen", "omoekrngoen", BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("newPersonNicknamesMessage")
  void givenGameAndNewPerson_whenEntryAttempt_shouldStorePlayerAndEntry(final String text, final String nickname,
                                                                        final BigDecimal expectedAmount) {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(text)
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
      .metadataEntities(List.of(MessageEntityCreator.domainMention("@%s".formatted(nickname))))
    );

    final var response = (SendMessage) entryTelegramService.storeEntry(messageMetadata);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(
        "Entries:\n - @%s -> %s\n".formatted(nickname, expectedAmount))
    );

    final var telegramPerson = telegramPersonServiceOld.getByNicknameAndChatId(nickname, CHAT_ID);
    final var actual = entriesRepository.findByGameAndPerson(GAME_ID, telegramPerson.getId());
    assertAll(
      () -> assertThat(actual).isNotEmpty(),
      () -> assertThat(actual.get().amounts()).hasSize(1),
      () -> assertThat(actual.get().amounts().get(0)).isEqualTo(expectedAmount)
    );
  }
}