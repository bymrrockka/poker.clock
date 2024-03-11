package by.mrrockka.features.accounting;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payout;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingTest {

  @Mock
  private TournamentCalculationStrategy strategy;

  @Test
  void givenStrategy_whenAccountingCalculateExecuted_thenStrategyShouldBeCalled() {
    final var accounting = new Accounting(List.of(strategy));
    final var game = GameCreator.tournament();
    final var expected = List.of(Payout.builder().build());

    when(strategy.isApplicable(game)).thenReturn(true);
    when(strategy.calculate(game)).thenReturn(expected);

    final var actual = accounting.calculate(game);

    assertThat(actual).isEqualTo(expected);
    when(strategy.castToType(game)).thenCallRealMethod();
    assertThat(strategy.<TournamentGame>castToType(game)).isExactlyInstanceOf(TournamentGame.class);
  }

}