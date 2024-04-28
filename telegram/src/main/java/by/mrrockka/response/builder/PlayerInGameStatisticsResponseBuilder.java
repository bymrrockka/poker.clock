package by.mrrockka.response.builder;

import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class PlayerInGameStatisticsResponseBuilder {

  public String response(final PlayerInGameStatistics statistics) {
    final var strBuilder = new StringBuilder("Player ");

    strBuilder
      .append(AT)
      .append(statistics.personEntries().person().getNickname())
      .append(" in game statistics:");

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("entries")
      .append(POINTER)
      .append(statistics.personEntries().entries().size());

    if (statistics.optPersonBounties().isPresent()) {
      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("bounties taken")
        .append(POINTER)
        .append(statistics.personBounties().taken().size());

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("bounties given")
        .append(POINTER)
        .append(statistics.personBounties().given().size());

      addTotalBuyInAmount(statistics.personEntries().total().multiply(BigDecimal.valueOf(2)), strBuilder);

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("money in game")
        .append(POINTER)
        .append(statistics.moneyInGame());
    } else {
      addTotalBuyInAmount(statistics.personEntries().total(), strBuilder);
    }

    if (statistics.optPersonWithdrawals().isPresent()) {
      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("total withdrawals amount")
        .append(POINTER)
        .append(statistics.personWithdrawals().total());

      strBuilder
        .append(NL)
        .append(TAB)
        .append(MINUS.stripLeading())
        .append("money total")
        .append(POINTER)
        .append(statistics.moneyInGame().negate());
    }

    return strBuilder.toString();
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
