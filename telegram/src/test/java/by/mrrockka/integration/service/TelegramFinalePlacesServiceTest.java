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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class TelegramFinalePlacesServiceTest {

  private static final Long CHAT_ID = 123L;
  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Instant GAME_TIMESTAMP = Instant.parse("2024-01-01T00:00:01Z");
  private static final String COMMAND = """
    /finaleplaces
    1 @king
    2 @queen
    3 @jack
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
        message.setPinnedMessage(MessageCreator.message(msg -> msg.setDate((int) GAME_TIMESTAMP.getEpochSecond())));
      })
    );

    final var response = (SendMessage) telegramFinalePlacesService.storePrizePool(update);

    assertAll(
      () -> assertThat(response).isNotNull(),
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getText()).isEqualTo("Finale places for game %s stored.".formatted(GAME_ID))
    );

    final var telegrams = List.of("king", "queen", "jack");
    final var telegramPersons = telegramPersonService.getAllByTelegramsAndChatId(telegrams, CHAT_ID);
    final var expected = new FinalePlaces(
      List.of(
        FinalPlace.builder()
          .person(telegramPersons.stream().filter(pers -> pers.getTelegram().equals("king")).findFirst().orElseThrow())
          .position(1)
          .build(),
        FinalPlace.builder()
          .person(telegramPersons.stream().filter(pers -> pers.getTelegram().equals("queen")).findFirst().orElseThrow())
          .position(2)
          .build(),
        FinalPlace.builder()
          .person(telegramPersons.stream().filter(pers -> pers.getTelegram().equals("jack")).findFirst().orElseThrow())
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