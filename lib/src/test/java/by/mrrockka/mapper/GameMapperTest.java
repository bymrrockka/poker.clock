package by.mrrockka.mapper;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.repo.game.GameType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

  private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

  @Test
  void givenEntityAndEntriesAndFinaleSummaryAndBounties_whenMapToTournamentExecuted_shouldReturnDomain() {
    assertThat(gameMapper.toTournament(GameCreator.entity(), GameCreator.ENTRIES, GameCreator.FINALE_SUMMARY))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.tournament());
  }

  @Test
  void givenEntityAndEntriesAndFinaleSummaryAndBounties_whenMapToBountyExecuted_shouldReturnDomain() {
    assertThat(
      gameMapper.toBounty(GameCreator.entity(), GameCreator.ENTRIES, GameCreator.BOUNTIES, GameCreator.FINALE_SUMMARY))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.bounty());
  }

  @Test
  void givenEntityAndEntriesAndWithdrawals_whenMapToCashExecuted_shouldReturnDomain() {
    assertThat(
      gameMapper.toCash(GameCreator.entity(), GameCreator.ENTRIES, GameCreator.WITHDRAWALS))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.cash());
  }


  @Test
  void givenTournament_whenMapExecuted_shouldReturnEntity() {
    assertThat(gameMapper.toEntity(GameCreator.tournament()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.entity(builder -> builder.bounty(BigDecimal.ZERO).gameType(GameType.TOURNAMENT)));
  }

  @Test
  void givenBounty_whenMapExecuted_shouldReturnEntity() {
    assertThat(gameMapper.toEntity(GameCreator.bounty()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.entity(builder -> builder.gameType(GameType.BOUNTY)));
  }

  @Test
  void givenCash_whenMapExecuted_shouldReturnEntity() {
    assertThat(gameMapper.toEntity(GameCreator.cash()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.entity(builder -> builder.bounty(BigDecimal.ZERO).gameType(GameType.CASH)));
  }

}