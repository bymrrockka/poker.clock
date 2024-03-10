package by.mrrockka.integration.repo;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.repo.game.TelegramGameEntity;
import by.mrrockka.repo.game.TelegramGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
public class TelegramGameRepositoryTest {

  private static final UUID GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");
  private static final Long CHAT_ID = 123L;
  private static final Instant CREATED_AT = Instant.now();

  @Autowired
  private TelegramGameRepository telegramGameRepository;

  @Test
  void givenGame_whenGameStoredInDb_shouldStoreChatIdAndCreationDate() {
    final var telegramGameEntity = TelegramGameEntity.builder()
      .gameId(GAME_ID)
      .chatId(CHAT_ID)
      .createdAt(CREATED_AT)
      .messageId(MessageCreator.MESSAGE_ID)
      .build();
    telegramGameRepository.save(telegramGameEntity);
    assertThat(telegramGameRepository.findByChatIdAndCreatedAt(CHAT_ID, CREATED_AT))
      .isEqualTo(Optional.of(GAME_ID));
  }
}
