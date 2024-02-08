package by.mrrockka.mapper;

import by.mrrockka.creator.GameCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

  GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

  @Test
  void givenEntityAndPlayers_whenMapExecuted_shouldReturnDomain() {
    assertThat(gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.domain());
  }

  @Test
  void givenEntityAndPlayersAndGameSummary_whenMapExecuted_shouldReturnDomain() {
    assertThat(gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS, GameCreator.GAME_SUMMARY))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.domain());
  }

  @Test
  void givenEntityAndPlayersAndGameSummaryAndBounties_whenMapExecuted_shouldReturnDomain() {
    assertThat(
      gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS, GameCreator.GAME_SUMMARY, GameCreator.BOUNTIES))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.domain());
  }

  @Test
  void givenDomain_whenMapExecuted_shouldReturnEntity() {
    assertThat(gameMapper.toEntity(GameCreator.domain()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.entity());
  }

}