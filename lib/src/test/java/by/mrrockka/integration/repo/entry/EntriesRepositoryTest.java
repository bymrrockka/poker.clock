package by.mrrockka.integration.repo.entry;

import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.repo.entry.EntriesEntity;
import by.mrrockka.repo.entry.EntriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class EntriesRepositoryTest {
  private static final UUID GAME_ID = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");

  @Autowired
  EntriesRepository entriesRepository;

  @Test
  void givenPersonAndGame_whenEntryStored_thenShouldReturnList() {
    final var personId = UUID.fromString("13b4108e-2dfa-4fea-8b7b-277e1c87d2d8");
    final var amount = BigDecimal.TEN;
    final var expected = EntriesEntity.builder()
      .personId(personId)
      .gameId(GAME_ID)
      .amounts(List.of(amount))
      .build();


    entriesRepository.save(GAME_ID, personId, amount);

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .contains(expected);
  }

  @Test
  void givenPersonAndGame_whenMultipleEntriesStored_thenShouldReturnFullList() {
    final var personId = UUID.fromString("72775968-3da6-469e-8a61-60104eacdb3a");
    final var amounts = List.of(
      BigDecimal.TEN,
      BigDecimal.ONE,
      BigDecimal.valueOf(3),
      BigDecimal.valueOf(1),
      BigDecimal.ZERO
    );
    final var expected = EntriesEntity.builder()
      .personId(personId)
      .gameId(GAME_ID)
      .amounts(amounts)
      .build();

    amounts.forEach(amount -> entriesRepository.save(GAME_ID, personId, amount));

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .contains(expected);
  }


  @Test
  void givenPersonAndGame_whenNoEntryStored_thenShouldReturnEmpty() {
    final var personId = UUID.fromString("e2691144-3b1b-4841-9693-fad7af25bba9");

    assertThat(entriesRepository.findByGameAndPerson(GAME_ID, personId))
      .isEmpty();
  }

}