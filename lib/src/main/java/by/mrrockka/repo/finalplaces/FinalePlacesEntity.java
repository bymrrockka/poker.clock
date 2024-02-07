package by.mrrockka.repo.finalplaces;

import by.mrrockka.repo.person.PersonEntity;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record FinalePlacesEntity(
  UUID gameId,
  Map<Integer, PersonEntity> places
) {
}
