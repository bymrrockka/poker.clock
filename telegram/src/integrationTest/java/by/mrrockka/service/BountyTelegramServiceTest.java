package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.mesageentity.MessageEntity;
import by.mrrockka.repo.bounty.BountyRepository;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class BountyTelegramServiceTest {

  private static final UUID GAME_ID = UUID.fromString("0075be9f-999d-4688-a773-cc986c14f787");
  private static final String ME_MENTION = "jackas";
  private static final Integer REPLY_TO_ID = 7;
  private static final Long CHAT_ID = 123L;

  @Autowired
  private BountyTelegramService bountyTelegramService;
  @Autowired
  private GameService gameService;
  @Autowired
  private BountyRepository bountyRepository;

  @Builder
  private record BountyArgument(String message, List<MessageEntity> entities,
                                Pair<TelegramPerson, TelegramPerson> fromAndTo) {}

  private static Stream<Arguments> bountyMessage() {
    return Stream.of(
      Arguments.of(BountyArgument.builder()
                     .message("/bounty @kinger kicked @queen")
                     .entities(List.of(
                       MessageEntityCreator.domainMention("@kinger"),
                       MessageEntityCreator.domainMention("@queen")
                     ))
                     .fromAndTo(Pair.of(
                       TelegramPerson.telegramPersonBuilder()
                         .id(UUID.randomUUID())
                         .nickname("queen")
                         .chatId(CHAT_ID)
                         .build(),
                       TelegramPerson.telegramPersonBuilder()
                         .id(UUID.randomUUID())
                         .nickname("kinger")
                         .chatId(CHAT_ID)
                         .build()
                     ))
                     .build())
    );
  }

  @ParameterizedTest
  @MethodSource("bountyMessage")
  void givenGameAndPerson_whenEntryAttempt_shouldStoreEntry(final BountyArgument argument) {
    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(argument.message())
      .entities(argument.entities())
      .replyTo(MessageMetadataCreator.domain(replyto -> replyto.id(REPLY_TO_ID)))
      .fromNickname(ME_MENTION)
    );

    final var bountyAmount = gameService.retrieveGame(GAME_ID).asType(BountyGame.class).getBountyAmount();
    final var response = (SendMessage) bountyTelegramService.storeBounty(messageMetadata);
    assertAll(
      () -> Assertions.assertThat(response).isNotNull(),
      () -> Assertions.assertThat(response.getChatId()).isEqualTo(String.valueOf(CHAT_ID)),
      () -> Assertions.assertThat(response.getText()).isEqualTo(
        "Bounty amount %s from %s stored for %s".formatted(bountyAmount, argument.fromAndTo().getKey().getNickname(),
                                                           argument.fromAndTo().getValue().getNickname()))
    );

    final var bountyEntity = bountyRepository.findAllByGameId(GAME_ID).stream()
      .filter(entity ->
                entity.from().getNickname().equals(argument.fromAndTo().getKey().getNickname()) &&
                  entity.to().getNickname().equals(argument.fromAndTo().getValue().getNickname()))
      .findFirst();

    assertAll(
      () -> Assertions.assertThat(bountyEntity).isNotEmpty(),
      () -> Assertions.assertThat(bountyEntity.get().amount()).isEqualTo(bountyAmount)
    );
  }

}