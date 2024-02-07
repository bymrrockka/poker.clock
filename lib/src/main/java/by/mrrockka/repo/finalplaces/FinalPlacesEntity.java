package by.mrrockka.repo.finalplaces;

import by.mrrockka.repo.person.PersonEntity;

import java.util.Map;
import java.util.UUID;

public record FinalPlacesEntity(
  UUID gameId,
  Map<Integer, PersonEntity> places
) {
}
