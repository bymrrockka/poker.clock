package by.mrrockka.integration.repo.prizepool;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.repo.prizepool.PrizePoolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
class PrizePoolRepositoryTest {

  private static final UUID TEST_GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");

  @Autowired
  PrizePoolRepository prizePoolRepository;

  @Test
  void givenPrizePoolEntity_whenStored_shouldBeAbleToGet() {
    final var expected = PrizePoolCreator.entity(builder -> builder.gameId(TEST_GAME_ID));
    prizePoolRepository.save(expected);
    assertThat(prizePoolRepository.findByGameId(TEST_GAME_ID))
      .contains(expected);
  }
}