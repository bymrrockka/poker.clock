package by.mrrockka.service.statistics;

import by.mrrockka.domain.MoneyTransfer;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.domain.statistics.GlobalPersonStatistics;
import by.mrrockka.service.FinalePlacesService;
import by.mrrockka.service.GameService;
import by.mrrockka.service.MoneyTransferService;
import by.mrrockka.service.PersonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

@Component
@RequiredArgsConstructor
public class GlobalPersonStatisticsService {

  private final MoneyTransferService moneyTransferService;
  private final FinalePlacesService finalePlacesService;
  private final PersonService personService;
  private final GameService gameService;

  public GlobalPersonStatistics retrieveStatistics(final String nickname) {

    final var person = personService.getByNickname(nickname);
    final var moneyTransfers = moneyTransferService.getForPerson(person);
    final var games = gameService.retrieveAllGames(moneyTransfers.stream().map(MoneyTransfer::gameId).toList());
    final var finalePlaces = finalePlacesService.getAllByPersonId(person.getId());

    final var tournamentGamesCount = games.stream()
      .filter(game -> game instanceof TournamentGame)
      .count();

    final var firstPlacesCount = finalePlaces.stream()
      .flatMap(places -> places.finalPlaces().stream())
      .filter(finalPlace -> 1 == finalPlace.position())
      .toList()
      .size();

    final var totalMoneyWon = moneyTransfers.stream()
      .filter(transfer -> transfer.type().equals(TransferType.CREDIT))
      .map(MoneyTransfer::amount)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var totalMoneyLose = moneyTransfers.stream()
      .filter(transfer -> transfer.type().equals(TransferType.DEBIT))
      .map(MoneyTransfer::amount)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var totalMoneyIn = totalMoneyIn(games, person);

    return GlobalPersonStatistics.builder()
      .person(person)
      .gamesPlayed(moneyTransfers.size())
      .totalMoneyIn(totalMoneyIn)
      .totalMoneyWon(totalMoneyWon)
      .totalMoneyLose(totalMoneyLose)
      .wonToLoseRatio(leftToRightRatio(totalMoneyWon, totalMoneyLose))
      .timesInPrizes(finalePlaces.size())
      .inPrizeRatio(leftToRightRatio(BigDecimal.valueOf(finalePlaces.size()), BigDecimal.valueOf(tournamentGamesCount)))
      .timesOnFirstPlace(firstPlacesCount)
      .build();
  }

  private BigDecimal leftToRightRatio(final @NonNull BigDecimal left, final @NonNull BigDecimal right) {
    return left.divide(right, 2, RoundingMode.DOWN)
      .multiply(BigDecimal.valueOf(100));
  }

  private BigDecimal totalMoneyIn(final List<Game> games, final Person person) {
    final var entriesTotal = games.stream()
      .flatMap(game -> game.getEntries().stream())
      .filter(personEntries -> personEntries.person().equals(person))
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var bountiesTotal = games.stream()
      .filter(game -> game.isType(BountyGame.class))
      .map(game -> game.asType(BountyGame.class))
      .collect(
        groupingBy(
          BountyGame::getBountyAmount,
          summingLong(
            game -> game.getEntries().stream()
              .filter(personEntries -> personEntries.person().equals(person))
              .count())))
      .entrySet().stream()
      .map(entry -> entry.getKey().multiply(BigDecimal.valueOf(entry.getValue())))
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    return entriesTotal.add(bountiesTotal);
  }


}
