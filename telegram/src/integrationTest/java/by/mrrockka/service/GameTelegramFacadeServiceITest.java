package by.mrrockka.service;

import by.mrrockka.config.TelegramPSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.repo.entries.EntriesEntity;
import by.mrrockka.repo.entries.EntriesRepository;
import by.mrrockka.repo.game.GameRepository;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.repo.game.TelegramGameRepository;
import by.mrrockka.repo.person.PersonEntity;
import by.mrrockka.repo.person.PersonRepository;
import by.mrrockka.repo.person.TelegramPersonEntity;
import by.mrrockka.repo.person.TelegramPersonRepository;
import by.mrrockka.service.game.GameTelegramFacadeService;
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

@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class GameTelegramFacadeServiceITest {

  private static final Long CHAT_ID = ChatCreator.CHAT_ID;
  private static final String TOURNAMENT_MESSAGE = """
    /tournament
    buyin: 30
    stack: 50k
    players:
    @capusta
    @kurva
    @asdasd
    """;

  @Autowired
  private GameTelegramFacadeService gameTelegramFacadeService;
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
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(TOURNAMENT_MESSAGE)
      .entities(List.of(
        MessageEntityCreator.domainMention("@capusta"),
        MessageEntityCreator.domainMention("@kurva"),
        MessageEntityCreator.domainMention("@asdasd")
      ))
    );

    final var response = (SendMessage) gameTelegramFacadeService.storeTournamentGame(messageMetadata);

    assertAll(
      () -> assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> assertThat(response.getReplyToMessageId()).isEqualTo(MessageCreator.MESSAGE_ID),
      () -> assertThat(response.getText()).isEqualTo("Tournament started.")
    );

    final var telegramGame = telegramGameRepository.findByChatAndMessageId(CHAT_ID, MessageCreator.MESSAGE_ID);
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
      "asdasd"
    );

    final var telegramPersonEntities = telegramPersonRepository.findAllByChatIdAndNicknames(nicknames, CHAT_ID);

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