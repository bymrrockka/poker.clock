package by.mrrockka.creator;

import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.repo.finalplaces.FinalePlacesEntity;
import by.mrrockka.repo.person.PersonEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class FinalePlacesCreator {

  public static final UUID GAME_ID = UUID.randomUUID();

  public static FinalePlacesEntity finalePlacesEntity() {
    return finalePlacesEntity(null);
  }

  public static FinalePlacesEntity finalePlacesEntity(
    Consumer<FinalePlacesEntity.FinalePlacesEntityBuilder> builderConsumer) {
    final var finalePlacesEntityBuilder = FinalePlacesEntity.builder()
      .gameId(GAME_ID)
      .places(places());

    if (nonNull(builderConsumer))
      builderConsumer.accept(finalePlacesEntityBuilder);

    return finalePlacesEntityBuilder.build();
  }

  public static FinalePlaces finalePlaces() {
    return new FinalePlaces(List.of(
      new FinalPlace(1, PersonCreator.domain()),
      new FinalPlace(2, PersonCreator.domain()),
      new FinalPlace(3, PersonCreator.domain())
    ));
  }

  private static Map<Integer, PersonEntity> places() {
    return Map.of(
      1, PersonCreator.entity(),
      2, PersonCreator.entity(),
      3, PersonCreator.entity()
    );
  }
}
