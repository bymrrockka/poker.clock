package by.mrrockka.response.builder;

import by.mrrockka.domain.prize.PrizePool;
import org.springframework.stereotype.Component;

import static by.mrrockka.response.builder.TextContants.NL;
import static by.mrrockka.response.builder.TextContants.TAB;

@Component
public class PrizePoolResponseBuilder {

  public String response(final PrizePool prizePool) {
    final var strBuilder = new StringBuilder("Prize Pool:");

    prizePool.positionAndPercentages()
      .forEach(pp -> strBuilder
        .append(NL)
        .append(TAB)
        .append("position: ").append(pp.position())
        .append(", percentage: ").append(pp.percentage()));
    return strBuilder.append(NL).toString();
  }
}
