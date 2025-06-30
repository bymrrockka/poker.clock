package by.mrrockka.service.calculation;

import by.mrrockka.creator.EntriesCreator;
import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.summary.finale.FinalePlaceSummary;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BountyCalculationStrategyTest {
  private static final BigDecimal BUY_IN = BigDecimal.valueOf(20);

  private final CalculationStrategy strategy = new BountyCalculationStrategy();

  @Test
  void givenPlayerBuyInEquallyAndWinnerGotAllBounties_thenShouldCreateListOfDebtorsWithRelatedBounties() {
    final var persons = List.of(
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom()
    );

    final var entries = List.of(
      EntriesCreator.entries(builder -> builder
        .person(persons.get(0))
        .entries(List.of(BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(1))
        .entries(List.of(BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(2))
        .entries(List.of(BUY_IN)))
    );

    final var bountyList = List.of(
      bounty(persons.get(0), persons.get(1), BUY_IN),
      bounty(persons.get(0), persons.get(2), BUY_IN)
    );

    final var game = GameCreator.bounty(builder -> builder
      .entries(entries)
      .bountyAmount(BUY_IN)
      .bountyList(bountyList)
      .finaleSummary(new FinaleSummary(List.of(
        finaleSummary(entries.get(0).person(), totalEntriesAmount(entries), 1)))
      )
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(0),
             PersonBounties.builder()
               .person(persons.get(0))
               .bounties(bountyList)
               .build(),
             List.of(
               debt(entries.get(1), bounties(persons.get(1), List.of(bountyList.get(0))),
                    BUY_IN.multiply(BigDecimal.valueOf(2))),
               debt(entries.get(2), bounties(persons.get(2), List.of(bountyList.get(1))),
                    BUY_IN.multiply(BigDecimal.valueOf(2)))
             ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndBountiesSpreadBetweenPlayers_thenShouldCreateListOfDebtorsWithRelatedBounties() {
    final var persons = List.of(
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom()
    );

    final var entries = List.of(
      EntriesCreator.entries(builder -> builder
        .person(persons.get(0))
        .entries(List.of(BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(1))
        .entries(List.of(BUY_IN, BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(2))
        .entries(List.of(BUY_IN)))
    );

    final var bountyList = List.of(
      bounty(persons.get(0), persons.get(1), BUY_IN),
      bounty(persons.get(0), persons.get(2), BUY_IN),
      bounty(persons.get(1), persons.get(0), BUY_IN)
    );

    final var game = GameCreator.bounty(builder -> builder
      .entries(entries)
      .bountyAmount(BUY_IN)
      .bountyList(bountyList)
      .finaleSummary(new FinaleSummary(List.of(
        finaleSummary(entries.get(1).person(), totalEntriesAmount(entries), 1)))
      )
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(1),
             PersonBounties.builder()
               .person(persons.get(1))
               .bounties(List.of(bountyList.get(0), bountyList.get(2)))
               .build(),
             List.of(
               debt(entries.get(2), bounties(persons.get(2), List.of(bountyList.get(1))),
                    BUY_IN.multiply(BigDecimal.valueOf(2)))
             )),
      payout(entries.get(0),
             PersonBounties.builder()
               .person(persons.get(0))
               .bounties(bountyList)
               .build(),
             Collections.emptyList())
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  @Test
  void givenPlayerBuyInEquallyAndBountiesEquallySpreadBetweenPlayers_thenShouldCreateListOfDebtorsWithRelatedBounties() {
    final var persons = List.of(
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom(),
      PersonCreator.domainRandom()
    );

    final var entries = List.of(
      EntriesCreator.entries(builder -> builder
        .person(persons.get(0))
        .entries(List.of(BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(1))
        .entries(List.of(BUY_IN))),
      EntriesCreator.entries(builder -> builder
        .person(persons.get(2))
        .entries(List.of(BUY_IN, BUY_IN)))
    );

    final var bountyList = List.of(
      bounty(persons.get(0), persons.get(1), BUY_IN),
      bounty(persons.get(1), persons.get(2), BUY_IN),
      bounty(persons.get(2), persons.get(0), BUY_IN)
    );

    final var game = GameCreator.bounty(builder -> builder
      .entries(entries)
      .bountyAmount(BUY_IN)
      .bountyList(bountyList)
      .finaleSummary(new FinaleSummary(List.of(
        finaleSummary(entries.get(2).person(), totalEntriesAmount(entries), 1)))
      )
    );

    final var actual = strategy.calculate(game);
    final var expect = List.of(
      payout(entries.get(2),
             PersonBounties.builder()
               .person(persons.get(2))
               .bounties(List.of(bountyList.get(1), bountyList.get(2)))
               .build(),
             List.of(
               debt(entries.get(0), bounties(persons.get(0), List.of(bountyList.get(0), bountyList.get(2))), BUY_IN),
               debt(entries.get(1), bounties(persons.get(1), List.of(bountyList.get(0), bountyList.get(1))), BUY_IN)
             ))
    );

    assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);
  }

  private FinalePlaceSummary finaleSummary(final Person person, final BigDecimal amount, final int position) {
    return FinalePlaceSummary.builder()
      .person(person)
      .position(position)
      .amount(amount)
      .build();
  }

  private Payout payout(final PersonEntries creditorEntries, final PersonBounties personBounties,
                        final List<Payer> payers) {
    return Payout.builder()
      .personEntries(creditorEntries)
      .personBounties(personBounties)
      .payers(payers)
      .build();
  }

  private Payer debt(final PersonEntries personEntries, final PersonBounties personBounties, final BigDecimal amount) {
    return Payer.builder()
      .personEntries(personEntries)
      .personBounties(personBounties)
      .amount(amount)
      .build();
  }

  private Bounty bounty(final Person to, final Person from, final BigDecimal amount) {
    return Bounty.builder()
      .to(to)
      .from(from)
      .amount(amount)
      .build();
  }

  private PersonBounties bounties(final Person person, final List<Bounty> bountyList) {
    return PersonBounties.builder()
      .person(person)
      .bounties(bountyList)
      .build();
  }

  private BigDecimal totalEntriesAmount(final List<PersonEntries> entriesList) {
    return entriesList.stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElseThrow();
  }

}