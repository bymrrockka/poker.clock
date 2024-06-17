package by.mrrockka.service.calculation;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationStrategyFactoryTest {

  @Mock
  private ApplicationContext applicationContext;
  @InjectMocks
  private CalculationStrategyFactory strategyFactory;

  @Test
  void givenUnknownGameType_whenGetStrategyInvoked_thenShouldThrowException() {
    when(applicationContext.getBean("tournamentCalculationStrategy", CalculationStrategy.class))
      .thenThrow(NoSuchBeanDefinitionException.class);

    assertThatThrownBy(() -> strategyFactory.getStrategy(GameCreator.tournament()))
      .isInstanceOf(NoStrategyFoundToCalculateGameException.class);
  }

  private static Stream<Arguments> appContextScenarios() {
    return Stream.of(
      Arguments.of(
        "tournamentCalculationStrategy",
        GameCreator.tournament(),
        new TournamentCalculationStrategy()
      ),
      Arguments.of(
        "bountyCalculationStrategy",
        GameCreator.bounty(),
        new BountyCalculationStrategy()
      ),
      Arguments.of(
        "cashCalculationStrategy",
        GameCreator.cash(),
        new CashCalculationStrategy()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("appContextScenarios")
  void givenGame_whenGetStrategyInvoked_thenShouldReturnStrategyV2(
    final String beanName,
    final Game game,
    final CalculationStrategy strategy) {

    when(applicationContext.getBean(beanName, CalculationStrategy.class)).thenReturn(strategy);

    assertThat(strategyFactory.getStrategy(game)).isEqualTo(strategy);
  }
}