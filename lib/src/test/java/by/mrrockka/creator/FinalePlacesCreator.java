package by.mrrockka.creator;

import by.mrrockka.repo.finalplaces.FinalePlacesEntity;
import by.mrrockka.repo.person.PersonEntity;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class FinalePlacesCreator {

  public static FinalePlacesEntity finalePlacesEntity() {
    return finalePlacesEntity(null);
  }

  public static FinalePlacesEntity finalePlacesEntity(
    Consumer<FinalePlacesEntity.FinalePlacesEntityBuilder> builderConsumer) {
    final var finalePlacesEntityBuilder = FinalePlacesEntity.builder()
      .gameId(UUID.randomUUID())
      .places(places());

    if (nonNull(builderConsumer))
      builderConsumer.accept(finalePlacesEntityBuilder);

    return finalePlacesEntityBuilder.build();
  }

  private static Map<Integer, PersonEntity> places() {
    return Map.of(
      1, PersonCreator.entity(),
      2, PersonCreator.entity(),
      3, PersonCreator.entity()
    );
  }
}
