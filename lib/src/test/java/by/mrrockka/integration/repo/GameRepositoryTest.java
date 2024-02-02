package by.mrrockka.integration.repo;

import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.integration.repo.creator.GameCreator;
import by.mrrockka.repo.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class GameRepositoryTest {

  @Autowired
  GameRepository gameRepository;

  @Test
  void givenEntity_whenSaved_shouldBeAbleToGetById() {
    final var expected = GameCreator.gameEntity();
    gameRepository.save(expected);

    assertThat(gameRepository.findById(expected.id(), expected.chatId())).isEqualTo(expected);
  }


}