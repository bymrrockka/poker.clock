package by.mrrockka.service;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PlayerCreator;
import by.mrrockka.mapper.GameMapper;
import by.mrrockka.repo.game.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();

  @Mock
  private GameMapper gameMapper;
  @Mock
  private GameRepository gameRepository;
  @Mock
  private GameSummaryService gameSummaryService;
  @Mock
  private PlayerService playerService;

  @InjectMocks
  private GameService gameService;

  @Test
  void givenGame_whenAttemptToSave_shouldMapAndCallRepo() {
    final var given = GameCreator.tournament();
    final var mapped = GameCreator.entity();

    when(gameMapper.toEntity(given)).thenReturn(mapped);
    gameService.storeNewGame(given);
    verify(gameRepository).save(mapped);
  }

  @Test
  void givenGameId_whenOnlyGameAndPlayersStored_shouldCallReposAndReturnOnlyGame() {
    final var players = List.of(PlayerCreator.player());
    final var expected = GameCreator.tournament(builder -> builder
      .tournamentGameSummary(null)
      .players(players)
    );
    final var given = GameCreator.entity();

    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(playerService.getAllForGame(GAME_ID)).thenReturn(players);
    when(gameMapper.toDomain(given, players, null)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID))
      .isEqualTo(expected);
  }


  @Test
  void givenGameId_whenGameAndPlayersStoredAndGameSummaryIsAssemblable_shouldCallReposAndReturnOnlyGame() {
    final var players = List.of(PlayerCreator.player());
    final var gameSummary = GameCreator.GAME_SUMMARY;
    final var expected = GameCreator.tournament(builder -> builder
      .tournamentGameSummary(gameSummary)
      .players(players)
    );
    final var given = GameCreator.entity();

    when(gameSummaryService.assembleGameSummary(GAME_ID, BigDecimal.ONE)).thenReturn(gameSummary);
    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(playerService.getAllForGame(GAME_ID)).thenReturn(players);
    when(gameMapper.toDomain(given, players, gameSummary)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID))
      .isEqualTo(expected);
  }

}