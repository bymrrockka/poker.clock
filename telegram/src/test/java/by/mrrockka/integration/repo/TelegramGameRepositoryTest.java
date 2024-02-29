package by.mrrockka.integration.repo;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.repo.game.TelegramGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
public class TelegramGameRepositoryTest {

  private static final UUID GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");
  private static final Long CHAT_ID = 123L;
  private static final Instant CREATED_AT = Instant.now();

  @Autowired
  private TelegramGameRepository telegramGameRepository;

  @Test
  void givenGame_whenGameStoredInDb_shouldStoreChatIdAndCreationDate() {
    telegramGameRepository.save(GAME_ID, CHAT_ID, CREATED_AT);
    assertThat(telegramGameRepository.findByChatIdAndCreatedAt(CHAT_ID, CREATED_AT))
      .isEqualTo(Optional.of(GAME_ID));
  }
}
