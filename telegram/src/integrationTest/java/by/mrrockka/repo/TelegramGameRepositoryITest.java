package by.mrrockka.repo;

import by.mrrockka.config.TelegramPSQLExtension;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.repo.game.TelegramGameEntity;
import by.mrrockka.repo.game.TelegramGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
public class TelegramGameRepositoryITest {

  private static final UUID GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");
  private static final Long CHAT_ID = 123L;

  @Autowired
  private TelegramGameRepository telegramGameRepository;

  @Test
  void givenGame_whenGameStoredInDb_shouldStoreChatIdAndCreationDate() {
    final var telegramGameEntity = TelegramGameEntity.builder()
      .gameId(GAME_ID)
      .chatId(CHAT_ID)
      .createdAt(Instant.now())
      .messageId(MessageCreator.MESSAGE_ID)
      .build();
    telegramGameRepository.save(telegramGameEntity);
    final var gameOpt = telegramGameRepository.findByChatAndMessageId(CHAT_ID, MessageCreator.MESSAGE_ID);
    assertThat(gameOpt.map(TelegramGameEntity::gameId))
      .contains(GAME_ID);
  }
}
