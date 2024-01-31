package by.mrrockka.repo.entities;

import java.util.Map;
import java.util.UUID;

public record FinalPlacesEntity(
  UUID gameId,
  Map<Integer, PersonEntity> places
) {
}
