package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.creator.UserCreator;
import by.mrrockka.repo.entries.EntriesRepository;
import by.mrrockka.service.TelegramEntryService;
import by.mrrockka.service.TelegramPersonService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramEntryServiceTest {

  private static final UUID GAME_ID = UUID.fromString("b759ac52-1496-463f-b0d8-982deeac085c");
  private static final Integer REPLY_TO_ID = 2;
  private static final Long CHAT_ID = 123L;

  @Autowired
  private TelegramEntryService telegramEntryService;
  @Autowired
  private TelegramPersonService telegramPersonService;
  @Autowired
  private EntriesRepository entriesRepository;

  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @kinger 60", "kinger", BigDecimal.valueOf(60)),
      Arguments.of("/entry @queen", "queen", BigDecimal.valueOf(15)),
      Arguments.of("/entry @me", "jackas", BigDecimal.valueOf(15))
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenGameAndPerson_whenEntryAttempt_shouldStoreEntry(String text, String telegram, BigDecimal expectedAmount) {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setText(text);
        message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(REPLY_TO_ID)));
        message.setFrom(UserCreator.user(telegram));
      })
    );

    final var response = (SendMessage) telegramEntryService.storeEntry(update);
    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(
        "%s enters the game. Entry amount is %s".formatted(telegram, expectedAmount))
    );

    final var telegramPerson = telegramPersonService.getByTelegramAndChatId(telegram, CHAT_ID);
    final var actual = entriesRepository.findByGameAndPerson(GAME_ID, telegramPerson.getId());
    assertAll(
      () -> assertThat(actual).isNotEmpty(),
      () -> assertThat(actual.get().amounts()).hasSize(1),
      () -> assertThat(actual.get().amounts().get(0)).isEqualTo(expectedAmount)
    );
  }
}