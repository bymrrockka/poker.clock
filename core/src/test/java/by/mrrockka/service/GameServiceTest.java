package by.mrrockka.service;

import by.mrrockka.creator.BountyCreator;
import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.WithdrawalsCreator;
import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.mapper.GameMapper;
import by.mrrockka.repo.game.GameRepository;
import by.mrrockka.repo.game.GameType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();
  private static final Instant FINISHED_AT = Instant.now();

  @Mock
  private GameMapper gameMapper;
  @Mock
  private GameRepository gameRepository;
  @Mock
  private TournamentSummaryService tournamentSummaryService;
  @Mock
  private EntriesService entriesService;
  @Mock
  private WithdrawalsService withdrawalsService;
  @Mock
  private BountyService bountyService;

  @InjectMocks
  private GameService gameService;

  @Test
  void givenTournamentGame_whenAttemptToSave_thenShouldStore() {
    final var given = GameCreator.tournament();
    final var mapped = GameCreator.entity(builder -> builder.gameType(GameType.TOURNAMENT));

    when(gameMapper.toEntity(given)).thenReturn(mapped);
    gameService.storeTournamentGame(given);
    verify(gameRepository).save(eq(mapped), any(Instant.class));
  }

  @Test
  void givenCashGame_whenAttemptToSave_thenShouldStore() {
    final var given = GameCreator.cash();
    final var mapped = GameCreator.entity(builder -> builder.gameType(GameType.CASH));

    when(gameMapper.toEntity(given)).thenReturn(mapped);
    gameService.storeCashGame(given);
    verify(gameRepository).save(eq(mapped), any(Instant.class));
  }

  @Test
  void givenBountyGame_whenAttemptToSave_thenShouldStore() {
    final var given = GameCreator.bounty();
    final var mapped = GameCreator.entity(builder -> builder.gameType(GameType.TOURNAMENT));

    when(gameMapper.toEntity(given)).thenReturn(mapped);
    gameService.storeBountyGame(given);
    verify(gameRepository).save(eq(mapped), any(Instant.class));
  }

  @Test
  void givenTounnamentGameId_whenOnlyGameAndEntriesStored_thenShouldReturnOnlyTournamentGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var expected = GameCreator.tournament(builder -> builder
      .finaleSummary(null)
      .entries(entries)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.TOURNAMENT).id(GAME_ID));

    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(gameMapper.toTournament(given, entries, null)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }


  @Test
  void givenTounnamentGameId_whenGameAndEntriesStoredAndGameSummaryIsAssemblable_thenShouldReturnTournamentGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var finaleSummary = GameCreator.FINALE_SUMMARY;
    final var expected = GameCreator.tournament(builder -> builder
      .finaleSummary(finaleSummary)
      .entries(entries)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.TOURNAMENT).id(GAME_ID));

    when(tournamentSummaryService.assembleTournamentSummary(GAME_ID, BigDecimal.ONE)).thenReturn(finaleSummary);
    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(gameMapper.toTournament(given, entries, finaleSummary)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }

  @Test
  void givenCashGameId_whenOnlyGameAndEntriesStored_thenShouldReturnOnlyCashGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var withdrawals = Collections.<PersonWithdrawals>emptyList();
    final var expected = GameCreator.cash(builder -> builder
      .entries(entries)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.CASH).id(GAME_ID));

    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(withdrawalsService.getAllForGame(GAME_ID)).thenReturn(withdrawals);
    when(gameMapper.toCash(given, entries, withdrawals)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }

  @Test
  void givenCashGameId_whenGameAndEntriesAndWithdrawalsStored_thenShouldReturnCashGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var withdrawals = List.of(WithdrawalsCreator.withdrawals());
    final var expected = GameCreator.cash(builder -> builder
      .entries(entries)
      .withdrawals(withdrawals)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.CASH).id(GAME_ID));

    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(withdrawalsService.getAllForGame(GAME_ID)).thenReturn(withdrawals);
    when(gameMapper.toCash(given, entries, withdrawals)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }

  @Test
  void givenBountyGameId_whenOnlyGameAndEntriesStored_thenShouldReturnOnlyGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var bounties = Collections.<Bounty>emptyList();
    final var expected = GameCreator.bounty(builder -> builder
      .entries(entries)
      .finaleSummary(null)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.BOUNTY).id(GAME_ID));

    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(bountyService.getAllForGame(GAME_ID)).thenReturn(bounties);
    when(gameMapper.toBounty(given, entries, bounties, null)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }


  @Test
  void givenBountyGameId_whenGameAndEntriesAndBountiesStored_thenShouldReturnGame() {
    final var entries = List.of(EntriesCreator.entries());
    final var bounties = List.of(BountyCreator.bounty());
    final var finaleSummary = GameCreator.FINALE_SUMMARY;
    final var expected = GameCreator.bounty(builder -> builder
      .entries(entries)
      .bountyList(bounties)
      .finaleSummary(finaleSummary)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.BOUNTY).id(GAME_ID));


    when(tournamentSummaryService.assembleTournamentSummary(GAME_ID, BigDecimal.ONE)).thenReturn(finaleSummary);
    when(gameRepository.findById(GAME_ID)).thenReturn(given);
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(bountyService.getAllForGame(GAME_ID)).thenReturn(bounties);
    when(gameMapper.toBounty(given, entries, bounties, finaleSummary)).thenReturn(expected);
    assertThat(gameService.retrieveGame(GAME_ID)).isEqualTo(expected);
  }

  @Test
  void givenGame_whenFinishGameExecuted_thenShouldAddFinishGameTimestamp() {
    final var game = GameCreator.tournament();
    gameService.finishGame(game);
    verify(gameRepository).finish(eq(game.getId()), any(Instant.class));
  }

  @Test
  void givenGameIds_whenRetrieveAllGamesCalled_thenShouldReturnListOfGames() {
    final var entries = List.of(EntriesCreator.entries());
    final var bounties = Collections.<Bounty>emptyList();
    final var expected = GameCreator.bounty(builder -> builder
      .entries(entries)
      .finaleSummary(null)
    );
    final var given = GameCreator.entity(builder -> builder.gameType(GameType.BOUNTY).id(GAME_ID));

    when(gameRepository.findAllByIds(List.of(GAME_ID))).thenReturn(List.of(given));
    when(entriesService.getAllForGame(GAME_ID)).thenReturn(entries);
    when(bountyService.getAllForGame(GAME_ID)).thenReturn(bounties);
    when(gameMapper.toBounty(given, entries, bounties, null)).thenReturn(expected);
    assertThat(gameService.retrieveAllGames(List.of(GAME_ID))).isEqualTo(List.of(expected));
  }

  @Test
  void givenGameWithoutFinishedAt_whenDoesGameHasUpdatesCalled_thenShouldReturnTrue() {
    final var game = GameCreator.tournament();
    assertThat(gameService.doesGameHasUpdates(game)).isTrue();
  }

  @Test
  void givenGameWithFinishedAtAndUpdatesWereStored_whenDoesGameHasUpdatesCalled_thenShouldReturnTrue() {
    final var game = GameCreator.tournament(tournament -> tournament.finishedAt(FINISHED_AT));
    when(gameRepository.hasUpdates(game.getId(), FINISHED_AT)).thenReturn(true);
    assertThat(gameService.doesGameHasUpdates(game)).isTrue();
  }

  @Test
  void givenGameWithFinishedAtAndNoUpdatesWereStored_whenDoesGameHasUpdatesCalled_thenShouldReturnFalse() {
    final var game = GameCreator.tournament(tournament -> tournament.finishedAt(FINISHED_AT));
    when(gameRepository.hasUpdates(game.getId(), FINISHED_AT)).thenReturn(false);
    assertThat(gameService.doesGameHasUpdates(game)).isFalse();
  }

}