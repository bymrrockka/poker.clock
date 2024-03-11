package by.mrrockka.mapper;

import by.mrrockka.creator.GameCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

  private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

  @Test
  void givenEntityAndPlayers_whenMapExecuted_shouldReturnDomain() {
    assertThat(gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.tournament());
  }

  @Test
  void givenEntityAndPlayersAndGameSummary_whenMapExecuted_shouldReturnDomain() {
    assertThat(gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS, GameCreator.GAME_SUMMARY))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.tournament());
  }
/*todo: uncomment when other types of game will be mapped

  @Test
  void givenEntityAndPlayersAndGameSummaryAndBounties_whenMapExecuted_shouldReturnDomain() {
    assertThat(
      gameMapper.toDomain(GameCreator.entity(), GameCreator.PLAYERS, GameCreator.GAME_SUMMARY, GameCreator.BOUNTIES))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.tournament());
  }
*/

  @Test
  void givenDomain_whenMapExecuted_shouldReturnEntity() {
    assertThat(gameMapper.toEntity(GameCreator.tournament()))
      .usingRecursiveComparison()
      .ignoringActualNullFields()
      .isEqualTo(GameCreator.entity(builder -> builder.bounty(BigDecimal.ZERO)));
  }

}