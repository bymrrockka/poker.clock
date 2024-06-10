package by.mrrockka.repo.finalplaces;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.repo.person.PersonRepository;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
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
      .stream()
      .map(entry -> MapEntry.entry(entry.getKey(), personRepository.findById(entry.getValue())))
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
      1, PersonCreator.entityRandom(),
      2, PersonCreator.entityRandom(),
      3, PersonCreator.entityRandom(),
      4, PersonCreator.entityRandom(),
      5, PersonCreator.entityRandom(),
      6, PersonCreator.entityRandom(),
      7, PersonCreator.entityRandom()
    );

    personRepository.saveAll(places.values().stream().toList());

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

  @Test
  void givenPersonId_whenDataInTable_shouldReturnAllFinalePlaces() {
    final var personEntity = PersonCreator.entity(
      builder -> builder.id(UUID.fromString("13b4108e-2dfa-4fea-8b7b-277e1c87d2d8"))
        .firstname(null)
        .lastname(null)
        .nickname(null));
    final var gameIds = List.of(UUID.fromString("fa3d03c4-f411-4852-810f-c0cc2f5b8c84"),
                                UUID.fromString("b759ac52-1496-463f-b0d8-982deeac085c"));

    final var places = Map.of(1, personEntity);
    final var expected = gameIds.stream()
      .map(gameId -> FinalePlacesEntity.builder()
        .places(places)
        .gameId(gameId)
        .build())
      .toList();

    assertThat(finalePlacesRepository.findAllByPersonId(personEntity.getId())).isEqualTo(expected);
  }

  @Test
  void givenPersonId_whenNoDataInTable_shouldReturnEmpty() {
    assertThat(finalePlacesRepository.findAllByPersonId(UUID.randomUUID()))
      .isEmpty();
  }
}