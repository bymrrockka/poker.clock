package by.mrrockka.repo.game;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.CorePSQLExtension;
import by.mrrockka.creator.GameCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CorePSQLExtension.class)
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

  @Test
  void givenIds_whenGetAllByIdsExecuted_thenShouldReturnRelatedGames() {
    final var expected = IntStream.range(0, 3)
      .mapToObj(i -> GameCreator.entity(builder -> builder.id(UUID.randomUUID())))
      .toList();
    expected.forEach(gameEntity -> gameRepository.save(gameEntity, Instant.now()));

    assertThat(gameRepository.findAllByIds(expected.stream().map(GameEntity::id).toList())).isEqualTo(expected);
  }
}