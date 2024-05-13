package by.mrrockka.repo.game;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.GameCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class GameRepositoryTest {

  @Autowired
  GameRepository gameRepository;

  @Test
  void givenEntity_whenSaved_shouldBeAbleToGetById() {
    final var expected = GameCreator.entity();
    gameRepository.save(expected, Instant.now());

    assertThat(gameRepository.findById(expected.id())).isEqualTo(expected);
  }


}