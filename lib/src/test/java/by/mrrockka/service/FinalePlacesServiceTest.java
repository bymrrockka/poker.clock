package by.mrrockka.service;

import by.mrrockka.creator.FinalePlacesCreator;
import by.mrrockka.mapper.FinalePlacesMapper;
import by.mrrockka.repo.finalplaces.FinalePlacesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalePlacesServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();

  @Mock
  private FinalePlacesRepository finalePlacesRepository;
  @Mock
  private FinalePlacesMapper finalePlacesMapper;
  @InjectMocks
  private FinalePlacesService finalePlacesService;

  @Test
  void givenGameAndFinalePlaces_whenAttemptToStore_shouldMapAndCallRepo() {
    final var given = FinalePlacesCreator.finalePlaces();
    final var mapped = FinalePlacesCreator.finalePlacesEntity();

    when(finalePlacesMapper.toEntity(GAME_ID, given)).thenReturn(mapped);
    finalePlacesService.store(GAME_ID, given);
    verify(finalePlacesRepository).save(mapped);
  }

  @Test
  void givenGameId_whenHasFinalePlaces_shouldCallRepoAndMap() {
    final var expected = FinalePlacesCreator.finalePlaces();
    final var given = FinalePlacesCreator.finalePlacesEntity();

    when(finalePlacesRepository.findByGameId(GAME_ID)).thenReturn(Optional.of(given));
    when(finalePlacesMapper.toDomain(given)).thenReturn(expected);
    assertThat(finalePlacesService.getByGameId(GAME_ID))
      .isEqualTo(expected);
  }

  @Test
  void givenGameId_whenNoFinalePlaces_shouldCallRepoAndReturnNull() {
    when(finalePlacesRepository.findByGameId(GAME_ID)).thenReturn(Optional.empty());

    assertThat(finalePlacesService.getByGameId(GAME_ID))
      .isNull();
    verifyNoInteractions(finalePlacesMapper);
  }


}