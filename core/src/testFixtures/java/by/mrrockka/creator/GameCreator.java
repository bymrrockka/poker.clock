package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.summary.finale.FinaleSummary;
import by.mrrockka.repo.game.GameEntity;
import by.mrrockka.domain.GameType;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameCreator {

  private static final Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final GameType GAME_TYPE = GameType.TOURNAMENT;
  public static final BigDecimal BUY_IN = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final BigDecimal STACK = BigDecimal.valueOf(FAKER.number().numberBetween(1500, 30000));
  public static final BigDecimal BOUNTY = BigDecimal.valueOf(FAKER.number().numberBetween(10, 100));
  public static final List<PersonEntries> ENTRIES = EntriesCreator.entriesList(2, BUY_IN);
  public static final List<PersonWithdrawals> WITHDRAWALS = WithdrawalsCreator.withdrawalsList(2, BUY_IN);
  public static final List<Bounty> BOUNTIES = List.of(BountyCreator.bounty());
  public static final FinaleSummary FINALE_SUMMARY = new FinaleSummary(List.of());

  public static TournamentGame tournament() {
    return tournament(null);
  }

  public static TournamentGame tournament(final Consumer<TournamentGame.TournamentGameBuilder> gameBuilderConsumer) {
    final var gameBuilder = TournamentGame.tournamentBuilder()
      .id(ID)
      .buyIn(BUY_IN)
      .stack(STACK)
      .entries(ENTRIES)
      .finaleSummary(FINALE_SUMMARY);

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static BountyGame bounty() {
    return bounty(null);
  }

  public static BountyGame bounty(final Consumer<BountyGame.BountyGameBuilder> gameBuilderConsumer) {
    final var gameBuilder = BountyGame.bountyBuilder()
      .id(ID)
      .buyIn(BUY_IN)
      .stack(STACK)
      .bountyAmount(BOUNTY)
      .entries(ENTRIES)
      .bountyList(BOUNTIES)
      .finaleSummary(FINALE_SUMMARY);

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static CashGame cash() {
    return cash(null);
  }

  public static CashGame cash(final Consumer<CashGame.CashGameBuilder> gameBuilderConsumer) {
    final var gameBuilder = CashGame.cashBuilder()
      .id(ID)
      .buyIn(BUY_IN)
      .stack(STACK)
      .entries(ENTRIES)
      .withdrawals(WITHDRAWALS);

    if (nonNull(gameBuilderConsumer))
      gameBuilderConsumer.accept(gameBuilder);

    return gameBuilder.build();
  }

  public static GameEntity entity() {
    return entity(null);
  }

  public static GameEntity entity(final Consumer<GameEntity.GameEntityBuilder> builderConsumer) {
    final var gameEntityBuilder = GameEntity.builder()
      .id(ID)
      .gameType(GAME_TYPE)
      .stack(STACK)
      .buyIn(BUY_IN)
      .bounty(BOUNTY);

    if (nonNull(builderConsumer))
      builderConsumer.accept(gameEntityBuilder);

    return gameEntityBuilder.build();
  }
}
