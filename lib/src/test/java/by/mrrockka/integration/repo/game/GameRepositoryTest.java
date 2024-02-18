package by.mrrockka.integration.repo.game;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.integration.repo.config.IntegrationTestConfiguration;
import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.repo.game.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = {IntegrationTestConfiguration.class})
class GameRepositoryTest {

  @Autowired
  GameRepository gameRepository;

  @Test
  void givenEntity_whenSaved_shouldBeAbleToGetById() {
    final var expected = GameCreator.entity();
    gameRepository.save(expected);

    assertThat(gameRepository.findById(expected.id())).isEqualTo(expected);
  }


}