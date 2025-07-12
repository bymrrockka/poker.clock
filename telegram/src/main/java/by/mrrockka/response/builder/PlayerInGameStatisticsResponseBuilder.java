package by.mrrockka.response.builder;

import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static by.mrrockka.response.builder.TextContants.*;

//todo: refactor
@Component
public class PlayerInGameStatisticsResponseBuilder {

  public String response(final PlayerInGameStatistics statistics) {
    final var strBuilder = new StringBuilder("Player ");
    var entries = statistics.entries().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO).multiply(
      BigDecimal.valueOf(2));

    strBuilder
      .append(AT)
      .append(statistics.player().getPerson().getNickname())
      .append(" in game statistics:");

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("entries")
      .append(POINTER)
      .append(statistics.entries().size());

    if (!statistics.bounties().isEmpty()) {
      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("bounties taken")
        .append(POINTER);
//        .append(statistics.bounties().taken().size());

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("bounties given")
        .append(POINTER);
//        .append(statistics.bounties().given().size());

      addTotalBuyInAmount(entries, strBuilder);

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("money in game")
        .append(POINTER)
        .append(statistics.moneyInGame());
    } else {
      addTotalBuyInAmount(entries, strBuilder);
    }

    if (!statistics.withdrawals().isEmpty()) {
      var withdrawals = statistics.withdrawals().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO).multiply(
        BigDecimal.valueOf(2));
      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("total withdrawals amount")
        .append(POINTER)
        .append(withdrawals);

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("money total")
        .append(POINTER)
        .append(statistics.moneyInGame().negate());
    }

    return strBuilder.append(NL).toString();
  }

  private void addTotalBuyInAmount(final BigDecimal amount, final StringBuilder strBuilder) {
    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total buy-in amount")
      .append(POINTER)
      .append(amount);
  }

}
