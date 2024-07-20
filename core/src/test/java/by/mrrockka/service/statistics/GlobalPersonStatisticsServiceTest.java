package by.mrrockka.service.statistics;

import by.mrrockka.creator.*;
import by.mrrockka.domain.MoneyTransfer;
import by.mrrockka.domain.statistics.GlobalPersonStatistics;
import by.mrrockka.service.FinalePlacesService;
import by.mrrockka.service.GameService;
import by.mrrockka.service.MoneyTransferService;
import by.mrrockka.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalPersonStatisticsServiceTest {

  @Mock
  private MoneyTransferService moneyTransferService;
  @Mock
  private FinalePlacesService finalePlacesService;
  @Mock
  private PersonService personService;
  @Mock
  private GameService gameService;
  @InjectMocks
  private GlobalPersonStatisticsService globalPersonStatisticsService;

  @Test
  void givenNickname_whenRetrieveStatisticsExecuted_thenShouldReturnStatistics() {
    final var person = PersonCreator.domainRandom();

    final var entries = EntriesCreator.entriesList(3, GameCreator.BUY_IN);
    final var personEntries = EntriesCreator.entries(
      builder -> builder.person(person).entries(List.of(GameCreator.BUY_IN)));
    entries.add(personEntries);

    final var moneyTransfers = List.of(MoneyTransferCreator.credit(), MoneyTransferCreator.credit(),
                                       MoneyTransferCreator.debit());
    final var games = List.of(
      GameCreator.tournament(builder -> builder.entries(entries)),
      GameCreator.bounty(builder -> builder.entries(entries)),
      GameCreator.cash(builder -> builder.entries(entries))
    );
    final var finalePlaces = List.of(FinalePlacesCreator.finalePlaces());

    when(personService.getByNickname(person.getNickname())).thenReturn(person);
    when(moneyTransferService.getForPerson(person)).thenReturn(moneyTransfers);
    when(gameService.retrieveAllGames(moneyTransfers.stream().map(MoneyTransfer::gameId).toList())).thenReturn(games);
    when(finalePlacesService.getAllByPersonId(person.getId())).thenReturn(finalePlaces);

    final var totalMoneyIn = GameCreator.BUY_IN.multiply(BigDecimal.valueOf(games.size())).add(GameCreator.BOUNTY);
    final var totalMoneyWon = MoneyTransferCreator.AMOUNT.multiply(BigDecimal.valueOf(2));
    final var totalMoneyLose = MoneyTransferCreator.AMOUNT;
    final var totalMoneyOut = totalMoneyIn.add(totalMoneyWon).subtract(totalMoneyLose);

    final var expected = GlobalPersonStatistics.builder()
      .person(person)
      .gamesPlayed(moneyTransfers.size())
      .totalMoneyIn(totalMoneyIn)
      .totalMoneyOut(totalMoneyOut)
      .totalMoneyWon(totalMoneyWon)
      .totalMoneyLose(totalMoneyLose)
      .outToInRatio(totalMoneyOut.divide(totalMoneyIn, 2, RoundingMode.DOWN))
      .timesInPrizes(finalePlaces.size())
      .inPrizeRatio(BigDecimal.valueOf(0.5).setScale(2))
      .timesOnFirstPlace(finalePlaces.size())
      .build();

    assertThat(globalPersonStatisticsService.retrieveStatistics(person.getNickname())).isEqualTo(expected);
  }
}
