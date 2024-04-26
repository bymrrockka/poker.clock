package by.mrrockka.response.builder;

import by.mrrockka.domain.finaleplaces.FinalePlaces;
import org.springframework.stereotype.Component;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class FinalePlacesResponseBuilder {

  public String response(final FinalePlaces finalePlaces) {
    final var strBuilder = new StringBuilder("Finale places:");

    finalePlaces.finalPlaces()
      .forEach(fp -> strBuilder
        .append(NL)
        .append(TAB)
        .append("position: ").append(fp.position())
        .append(", telegram: ").append(AT).append(fp.person().getNickname()));

    return strBuilder.toString();
  }
}
