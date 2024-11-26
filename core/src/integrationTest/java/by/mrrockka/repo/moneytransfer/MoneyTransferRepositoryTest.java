package by.mrrockka.repo.moneytransfer;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.CorePSQLExtension;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.MoneyTransferCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.repo.game.GameRepository;
import by.mrrockka.repo.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CorePSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class MoneyTransferRepositoryTest {

  @Autowired
  private PersonRepository personRepository;
  @Autowired
  private GameRepository gameRepository;
  @Autowired
  private MoneyTransferRepository moneyTransferRepository;

  @Test
  void givenEntity_whenSaveExecuted_thenShouldStore() {
    final var game = GameCreator.entity(builder -> builder.id(UUID.randomUUID()));
    gameRepository.save(game, Instant.now());

    final var person = PersonCreator.entityRandom();
    personRepository.save(person);

    final var expected = MoneyTransferCreator.entity(builder -> builder.gameId(game.id()).personId(person.getId()));
    moneyTransferRepository.save(expected, Instant.now());
    assertThat(moneyTransferRepository.getForPerson(expected.personId())).isEqualTo(List.of(expected));
  }

  @Test
  void givenEntities_whenSaveAllExecuted_thenShouldStore() {
    final var person = PersonCreator.entityRandom();
    personRepository.save(person);

    final var expected =
      IntStream.range(0, 3)
        .mapToObj(i -> MoneyTransferCreator.entity(builder -> builder
          .gameId(UUID.randomUUID())
          .personId(person.getId())))
        .toList();

    expected.forEach(
      moneyTransfer -> gameRepository
        .save(GameCreator.entity(builder -> builder.id(moneyTransfer.gameId())), Instant.now()));

    moneyTransferRepository.saveAll(expected, Instant.now());
    assertThat(moneyTransferRepository.getForPerson(person.getId())).isEqualTo(expected);
  }

}