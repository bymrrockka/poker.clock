package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class FinalePlacesTelegramServiceITest {

  private static final Long CHAT_ID = 123L;
  private static final UUID GAME_ID = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
  private static final Integer REPLY_TO_ID = 1;
  private static final String TEXT = """
    /finaleplaces
    1 @kinger
    2 @queen
    3 @jackas
    """;

  @Autowired
  private FinalePlacesTelegramService finalePlacesTelegramService;

  @Autowired
  private FinalePlacesService finalePlacesService;
  @Autowired
  private TelegramPersonService telegramPersonService;

  @Test
  void givenGameIdAndChatId_whenFinalePlacesMessageConsumed_shouldMapAndStoreFinalePlacesAgainstTheGameId() {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(TEXT)
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
      .entities(List.of(
        MessageEntityCreator.domainMention("@kinger"),
        MessageEntityCreator.domainMention("@queen"),
        MessageEntityCreator.domainMention("@jackas")
      ))
    );

    final var response = (SendMessage) finalePlacesTelegramService.storePrizePool(messageMetadata);
    final var expectedMessage = """
      Finale places:
      	position: 1, telegram: @kinger
      	position: 2, telegram: @queen
      	position: 3, telegram: @jackas
      	""";

    assertAll(
      () -> Assertions.assertThat(response).isNotNull(),
      () -> Assertions.assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> Assertions.assertThat(response.getText()).isEqualTo(expectedMessage)
    );

    final var nicknames = List.of("kinger", "queen", "jackas");
    final var telegramPersons = telegramPersonService.getAllByNicknamesAndChatId(nicknames, CHAT_ID);
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
      () -> Assertions.assertThat(actual).isNotNull(),
      () -> Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected)
    );
  }
}