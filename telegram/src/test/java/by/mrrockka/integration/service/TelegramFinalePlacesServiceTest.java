package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.service.FinalePlacesService;
import by.mrrockka.service.TelegramFinalePlacesService;
import by.mrrockka.service.TelegramPersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramFinalePlacesServiceTest {

  private static final Long CHAT_ID = 123L;
  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Integer REPLY_TO_ID = 1;
  private static final String COMMAND = """
    /finaleplaces
    1 @kinger
    2 @queen
    3 @jackas
    """;

  @Autowired
  private TelegramFinalePlacesService telegramFinalePlacesService;

  @Autowired
  private FinalePlacesService finalePlacesService;
  @Autowired
  private TelegramPersonService telegramPersonService;

  @Test
  void givenGameIdAndChatId_whenFinalePlacesMessageConsumed_shouldMapAndStoreFinalePlacesAgainstTheGameId() {
    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setText(COMMAND);
        message.setChat(ChatCreator.chat(CHAT_ID));
        message.setReplyToMessage(MessageCreator.message(msg -> msg.setMessageId(REPLY_TO_ID)));
      })
    );

    final var response = (SendMessage) telegramFinalePlacesService.storePrizePool(update);
    final var expectedMessage = """
      Finale places:
      	position: 1, telegram: @kinger
      	position: 2, telegram: @queen
      	position: 3, telegram: @jackas
      	""";

    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo(expectedMessage)
    );

    final var telegrams = List.of("kinger", "queen", "jackas");
    final var telegramPersons = telegramPersonService.getAllByTelegramsAndChatId(telegrams, CHAT_ID);
    final var expected = new FinalePlaces(
      List.of(
        FinalPlace.builder()
          .person(
            telegramPersons.stream().filter(pers -> pers.getNickname().equals("kinger")).findFirst().orElseThrow())
          .position(1)
          .build(),
        FinalPlace.builder()
          .person(telegramPersons.stream().filter(pers -> pers.getNickname().equals("queen")).findFirst().orElseThrow())
          .position(2)
          .build(),
        FinalPlace.builder()
          .person(
            telegramPersons.stream().filter(pers -> pers.getNickname().equals("jackas")).findFirst().orElseThrow())
          .position(3)
          .build()
      ));
    final var actual = finalePlacesService.getByGameId(GAME_ID);

    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected)
    );
  }
}