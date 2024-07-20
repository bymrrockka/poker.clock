package by.mrrockka.response.builder;

import by.mrrockka.domain.statistics.GlobalPersonStatistics;
import org.springframework.stereotype.Component;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class GlobalPersonStatisticsResponseBuilder {

  public String response(final GlobalPersonStatistics details) {
    final var strBuilder = new StringBuilder("Player ");

    strBuilder
      .append(AT)
      .append(details.person().getNickname())
      .append(" global statistics:");

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("games played")
      .append(POINTER)
      .append(details.gamesPlayed());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total money won")
      .append(POINTER)
      .append(details.totalMoneyWon());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total money lose")
      .append(POINTER)
      .append(details.totalMoneyLose());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total money in")
      .append(POINTER)
      .append(details.totalMoneyIn());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("total money out")
      .append(POINTER)
      .append(details.totalMoneyOut());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("out to in ratio")
      .append(POINTER)
      .append(details.outToInRatio());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("times in prizes")
      .append(POINTER)
      .append(details.timesInPrizes());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("times on first place")
      .append(POINTER)
      .append(details.timesOnFirstPlace());

    strBuilder
      .append(NL)
      .append(TAB)
      .append(MINUS.stripLeading())
      .append("in prize ratio")
      .append(POINTER)
      .append(details.inPrizeRatio());

    return strBuilder.append(NL).toString();
  }

}
