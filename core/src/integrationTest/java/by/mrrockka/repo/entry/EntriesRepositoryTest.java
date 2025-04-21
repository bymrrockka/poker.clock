package by.mrrockka.repo.entry;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.extension.CorePSQLExtension;
import by.mrrockka.repo.entries.EntriesEntity;
import by.mrrockka.repo.entries.EntriesRepository;
import by.mrrockka.repo.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CorePSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class EntriesRepositoryTest {
  private static final UUID GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");

  @Autowired
  EntriesRepository entriesRepository;
  @Autowired
  PersonRepository personRepository;

  @Test
  void givenPersonAndGame_whenEntryStored_thenShouldReturnList() {
    final var personId = UUID.fromString("13b4108e-2dfa-4fea-8b7b-277e1c87d2d8");
    final var person = personRepository.findById(personId);
    final var amount = BigDecimal.TEN;
    final var expected = EntriesEntity.builder()
      .person(person)
      .gameId(GAME_ID)
      .amounts(List.of(amount))
      .build();

    entriesRepository.save(GAME_ID, personId, amount, Instant.now());

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .contains(expected);
  }

  @Test
  void givenPersonAndGame_whenMultipleEntriesStored_thenShouldReturnFullList() {
    final var personId = UUID.fromString("72775968-3da6-469e-8a61-60104eacdb3a");
    final var person = personRepository.findById(personId);
    final var amounts = List.of(
      BigDecimal.TEN,
      BigDecimal.ONE,
      BigDecimal.valueOf(3),
      BigDecimal.valueOf(1),
      BigDecimal.ZERO
    );
    final var expected = EntriesEntity.builder()
      .person(person)
      .gameId(GAME_ID)
      .amounts(amounts)
      .build();

    amounts.forEach(amount -> entriesRepository.save(GAME_ID, personId, amount, Instant.now()));

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .contains(expected);
  }

  @Test
  void givenPersonsAndGame_whenEntriesStored_thenShouldReturnFullListByGameId() {
    final var gameId = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
    final var firstPersonId = UUID.fromString("e2691144-3b1b-4841-9693-fad7af25bba9");
    final var secondPersonId = UUID.fromString("58ae9984-1ebc-4621-ba0e-a577c69283ef");
    final var firstPerson = personRepository.findById(firstPersonId);
    final var secondPerson = personRepository.findById(secondPersonId);
    final var firstAmounts = List.of(
      BigDecimal.TEN,
      BigDecimal.ONE,
      BigDecimal.valueOf(3),
      BigDecimal.valueOf(1),
      BigDecimal.ZERO
    );
    final var secondAmounts = List.of(
      BigDecimal.valueOf(3),
      BigDecimal.valueOf(1),
      BigDecimal.ONE
    );

    final var expected =
      List.of(
        EntriesEntity.builder()
          .person(firstPerson)
          .gameId(gameId)
          .amounts(firstAmounts)
          .build(),
        EntriesEntity.builder()
          .person(secondPerson)
          .gameId(gameId)
          .amounts(secondAmounts)
          .build()
      );

    firstAmounts.forEach(amount -> entriesRepository.save(gameId, firstPersonId, amount, Instant.now()));
    secondAmounts.forEach(amount -> entriesRepository.save(gameId, secondPersonId, amount, Instant.now()));

    assertThat(entriesRepository.findAllByGameId(gameId))
      .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  void givenPersonAndGame_whenNoEntryStored_thenShouldReturnEmpty() {
    final var personId = UUID.randomUUID();

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .isEmpty();
  }

}