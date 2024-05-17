package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.*;
import by.mrrockka.repo.entries.EntriesEntity;
import by.mrrockka.repo.entries.EntriesRepository;
import by.mrrockka.repo.game.GameRepository;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.repo.game.TelegramGameRepository;
import by.mrrockka.repo.person.PersonEntity;
import by.mrrockka.repo.person.PersonRepository;
import by.mrrockka.repo.person.TelegramPersonEntity;
import by.mrrockka.repo.person.TelegramPersonRepository;
import by.mrrockka.service.game.TelegramGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class TelegramGameServiceTest {

  private static final String TOURNAMENT_MESSAGE = """
    /tournament
    buy-in: 30
    stack: 50k
    players:
    @capusta
    @kurva
    @asdasd
    @me
    """;

  @Autowired
  private TelegramGameService telegramGameService;
  @Autowired
  private TelegramGameRepository telegramGameRepository;
  @Autowired
  private GameRepository gameRepository;
  @Autowired
  private EntriesRepository entriesRepository;
  @Autowired
  private TelegramPersonRepository telegramPersonRepository;
  @Autowired
  private PersonRepository personRepository;

  @Test
  void giveTournamentMessage_whenAttemptToStored_shouldStoreGameConnectedToChatIdAndPersonsAndEntriesForPersons() {
    final var message = MessageCreator.message(msg -> {
      msg.setText(TOURNAMENT_MESSAGE);
      msg.setEntities(List.of(
        MessageEntityCreator.apiCommand(TOURNAMENT_MESSAGE, "/tournament"),
        MessageEntityCreator.apiMention(TOURNAMENT_MESSAGE, "@capusta"),
        MessageEntityCreator.apiMention(TOURNAMENT_MESSAGE, "@kurva"),
        MessageEntityCreator.apiMention(TOURNAMENT_MESSAGE, "@asdasd")
      ));
    });

    final var update = UpdateCreator.update(message);
    final var chatId = ChatCreator.CHAT_ID;

    final var response = (SendMessage) telegramGameService.storeTournamentGame(update);

    assertAll(
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(chatId)),
      () -> assertThat(response.getReplyToMessageId()).isEqualTo(MessageCreator.MESSAGE_ID),
      () -> assertThat(response.getText()).isEqualTo("Tournament started.")
    );

    final var telegramGame = telegramGameRepository.findByChatAndMessageId(chatId, MessageCreator.MESSAGE_ID);
    assertThat(telegramGame).isNotEmpty();

    final var gameEntity = gameRepository.findById(telegramGame.get().gameId());
    assertAll(
      () -> assertThat(gameEntity).isNotNull(),
      () -> assertThat(gameEntity.gameType()).isEqualTo(GameType.TOURNAMENT),
      () -> assertThat(gameEntity.buyIn()).isEqualTo(BigDecimal.valueOf(30)),
      () -> assertThat(gameEntity.stack()).isEqualTo(BigDecimal.valueOf(50000))
    );

    final var nicknames = List.of(
      "capusta",
      "kurva",
      "asdasd",
      UserCreator.USER_NAME
    );

    final var telegramPersonEntities = telegramPersonRepository.findAllByChatIdAndNicknames(chatId, nicknames);

    assertAll(
      () -> assertThat(telegramPersonEntities).isNotEmpty(),
      () -> assertThat(telegramPersonEntities.stream().map(TelegramPersonEntity::getNickname).toList())
        .containsExactlyInAnyOrderElementsOf(nicknames)
    );

    final var personEntities = personRepository.findAllByIds(
      telegramPersonEntities.stream().map(TelegramPersonEntity::getId).toList());

    assertAll(
      () -> assertThat(personEntities).isNotEmpty(),
      () -> assertThat(telegramPersonEntities.stream().map(TelegramPersonEntity::getId).toList())
        .containsExactlyInAnyOrderElementsOf(personEntities.stream().map(PersonEntity::getId).toList())
    );

    final var entriesEntities = entriesRepository.findAllByGameId(telegramGame.get().gameId());
    assertAll(
      () -> assertThat(entriesEntities).isNotEmpty(),
      () -> assertThat(entriesEntities.stream().map(EntriesEntity::person).toList())
        .containsExactlyInAnyOrderElementsOf(personEntities),
      () -> entriesEntities.forEach(entriesEntity -> assertThat(entriesEntity.amounts())
        .isEqualTo(List.of(BigDecimal.valueOf(30))))
    );
  }
}