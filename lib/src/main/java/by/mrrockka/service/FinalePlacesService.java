package by.mrrockka.service;

import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.mapper.FinalePlacesMapper;
import by.mrrockka.repo.finalplaces.FinalePlacesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

//todo: add int tests
@Service
@RequiredArgsConstructor
public class FinalePlacesService {

  private final FinalePlacesRepository finalePlacesRepository;
  private final FinalePlacesMapper finalePlacesMapper;

  public FinalePlaces retrieveFinalePlaces(@NonNull UUID gameId) {
    return finalePlacesRepository.findByGameId(gameId)
      .map(finalePlacesMapper::toDomain)
      .orElse(null);
  }

  public void storeFinalePlaces(@NonNull UUID gameId, @NonNull FinalePlaces finalePlaces) {
    finalePlacesRepository.save(finalePlacesMapper.toEntity(gameId, finalePlaces));
  }
}
