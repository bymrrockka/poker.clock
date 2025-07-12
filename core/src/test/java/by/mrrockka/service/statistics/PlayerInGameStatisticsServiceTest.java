package by.mrrockka.service.statistics;

import by.mrrockka.creator.*;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PlayerInGameStatisticsServiceTest {

  private static final BigDecimal BUYIN = BigDecimal.ONE;
  private static final BigDecimal WITHDRAWAL = BigDecimal.ONE;
  private static final BigDecimal BOUNTY = BigDecimal.ONE;
  private static final Person PERSON = PersonCreator.domainRandom();
  private final PlayerInGameStatisticsService playerInGameStatisticsService = new PlayerInGameStatisticsService();

  @Test
  //todo: refactor
  void givenTournament_whenRetrieveStatisticsInvoked_thenShouldReturnStatistics() {
    final var entries = EntriesCreator.entriesList(10, BUYIN);
    final var personEntries = EntriesCreator.entries(builder -> builder.person(PERSON));

    entries.add(personEntries);

    final var game = GameCreator.tournament(builder -> builder.entries(entries));
    final var expected = PlayerInGameStatistics.builder()
//      .entries(personEntries)
      .moneyInGame(personEntries.total())
      .build();

//    assertThat(playerInGameStatisticsService.retrieveStatistics(game, PERSON.getNickname()))
//      .isEqualTo(expected);
  }

  @Test
  void givenBounty_whenRetrieveStatisticsInvoked_thenShouldReturnStatistics() {
    final var entries = EntriesCreator.entriesList(10, BUYIN);
    final var personEntries = EntriesCreator.entries(builder -> builder.person(PERSON));
    final var bountyList = BountyCreator.bountiesList(10, BOUNTY);
    final var bountiesWithPerson = List.of(
      BountyCreator.bounty(builder -> builder.from(PERSON).to(PersonCreator.domainRandom()).amount(BOUNTY)),
      BountyCreator.bounty(builder -> builder.to(PERSON).from(PersonCreator.domainRandom()).amount(BOUNTY)),
      BountyCreator.bounty(builder -> builder.to(PERSON).from(PersonCreator.domainRandom()).amount(BOUNTY))
    );

    entries.add(personEntries);
    bountyList.addAll(bountiesWithPerson);

    final var personBounties = PersonBounties.builder()
      .bounties(bountiesWithPerson)
      .person(PERSON)
      .build();

    final var game = GameCreator.bounty(builder -> builder.entries(entries).bountyList(bountyList));
    final var expected = PlayerInGameStatistics.builder()
//      .personEntries(personEntries)
//      .personBounties(personBounties)
      .moneyInGame(
        personEntries.total()
          .add(game.getBountyAmount().multiply(BigDecimal.valueOf(personEntries.entries().size())))
          .subtract(personBounties.totalTaken())
      ).build();

//    assertThat(playerInGameStatisticsService.retrieveStatistics(game, PERSON.getNickname()))
//      .isEqualTo(expected);
  }

  @Test
  void givenCashGame_whenRetrieveStatisticsInvoked_thenShouldReturnStatistics() {
    final var entries = EntriesCreator.entriesList(10, BUYIN);
    final var withdrawals = WithdrawalsCreator.withdrawalsList(10, WITHDRAWAL);
    final var personEntries = EntriesCreator.entries(builder -> builder.person(PERSON));
    final var personWithdrawals = WithdrawalsCreator.withdrawals(builder -> builder.person(PERSON));

    entries.add(personEntries);
    withdrawals.add(personWithdrawals);

    final var game = GameCreator.cash(builder -> builder.entries(entries).withdrawals(withdrawals));
    final var expected = PlayerInGameStatistics.builder()
//      .personEntries(personEntries)
//      .personWithdrawals(personWithdrawals)
      .moneyInGame(personEntries.total().subtract(personWithdrawals.total()))
      .build();

//    assertThat(playerInGameStatisticsService.retrieveStatistics(game, PERSON.getNickname()))
//      .isEqualTo(expected);
  }

  @Test
  void givenGame_whenGameHasNoPersonEntries_thenShouldThrowException() {
    final var entries = EntriesCreator.entriesList(10, BUYIN);
    final var game = GameCreator.cash(builder -> builder.entries(entries));

//    assertThatThrownBy(() -> playerInGameStatisticsService.retrieveStatistics(game, PERSON.getNickname()))
//      .isInstanceOf(PersonIsNotInGameException.class);
  }
}