package by.mrrockka.integration.repo.finalplaces;

import by.mrrockka.creator.PersonCreator;
import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.repo.finalplaces.FinalePlacesEntity;
import by.mrrockka.repo.finalplaces.FinalePlacesRepository;
import by.mrrockka.repo.person.PersonRepository;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class FinalePlacesRepositoryTest {

  private static final Map<Integer, UUID> PLACES = Map.of(
    1, UUID.fromString("13b4108e-2dfa-4fea-8b7b-277e1c87d2d8"),
    2, UUID.fromString("72775968-3da6-469e-8a61-60104eacdb3a")
  );

  @Autowired
  FinalePlacesRepository finalePlacesRepository;

  @Autowired
  PersonRepository personRepository;

  @Test
  void givenFinalePlaces_whenDbHaveEntity_shouldBeAbleToGetByGameId() {
    final var gameId = UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84");
    final var places = PLACES.entrySet()
      .stream().map(entry -> MapEntry.entry(entry.getKey(), personRepository.findById(entry.getValue())))
               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    final var expected = FinalePlacesEntity.builder()
      .gameId(gameId)
      .places(places)
      .build();

    assertThat(finalePlacesRepository.findByGameId(gameId))
      .contains(expected);
  }

  @Test
  void givenFinalePlaces_whenStored_shouldBeAbleToGetByGameId() {
    final var places = Map.of(
      1, PersonCreator.entity(),
      2, PersonCreator.entity(),
      3, PersonCreator.entity(),
      4, PersonCreator.entity(),
      5, PersonCreator.entity(),
      6, PersonCreator.entity(),
      7, PersonCreator.entity()
    );

    personRepository.saveAll(places.values()
                               .stream().toList());

    final var gameId = UUID.fromString("4a411a12-2386-4dce-b579-d806c91d6d17");
    final var expected = FinalePlacesEntity.builder()
      .gameId(gameId)
      .places(places)
      .build();

    finalePlacesRepository.save(expected);

    assertThat(finalePlacesRepository.findByGameId(gameId))
      .contains(expected);
  }

  @Test
  void givenGameId_whenNoDataInTable_shouldReturnEmpty() {
    assertThat(finalePlacesRepository.findByGameId(UUID.randomUUID()))
      .isEmpty();
  }
}