package by.mrrockka.service.calculation;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PayoutCreator;
import by.mrrockka.service.GameServiceOld;
import by.mrrockka.service.MoneyTransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//todo: remove or refactor
class CalculationServiceTest {

  @Mock
  private TournamentCalculationStrategy strategy;
  @Mock
  private MoneyTransferService moneyTransferService;
  @Mock
  private GameServiceOld gameService;
  @Mock
  private CalculationStrategyFactory calculationStrategyFactory;
  @Mock
  private GameCalculator gameCalculator;
  @InjectMocks
  private CalculationService calculationService;

  @Test
  void givenStrategy_whenCalculatePayoutsExecuted_thenShouldCalculateAndStoreNewPayouts() {
    final var game = GameCreator.tournament();
    final var expected = List.of(PayoutCreator.payout());

//    when(calculationStrategyFactory.getStrategy(game)).thenReturn(strategy);
//    when(strategy.calculate(game)).thenReturn(expected);
//    when(gameService.doesGameHasUpdates(game)).thenReturn(true);

//    final var actual = calculationService.calculateAndSave(game);

//    assertThat(actual).isEqualTo(expected);

    verify(moneyTransferService).storeBatch(game, expected);
    verify(gameService).finishGame(game);
  }

  @Test
  void givenStrategy_whenCalculatePayoutsExecutedAndGameHasNoUpdates_thenShouldNotStoredCalculatedPayouts() {
    final var game = GameCreator.tournament();
    final var expected = List.of(PayoutCreator.payout());

//    when(calculationStrategyFactory.getStrategy(game)).thenReturn(strategy);
//    when(strategy.calculate(game)).thenReturn(expected);
//    when(gameService.doesGameHasUpdates(game)).thenReturn(false);

//    final var actual = calculationService.calculateAndSave(game);

//    assertThat(actual).isEqualTo(expected);

    verifyNoInteractions(moneyTransferService);
    verifyNoMoreInteractions(gameService);
  }

}