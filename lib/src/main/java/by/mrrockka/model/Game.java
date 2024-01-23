package by.mrrockka.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record Game(

  GameType gameType,
  BigDecimal buyIn,
  BigDecimal stack,
  List<Person> persons
) {
}
