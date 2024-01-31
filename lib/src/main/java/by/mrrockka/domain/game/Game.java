package by.mrrockka.domain.game;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.FinalPlaces;
import by.mrrockka.domain.Person;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record Game(
  UUID id,
  String chatId,
  GameType gameType,
  BigDecimal buyIn,
  BigDecimal stack,
  List<Person> persons,
  FinalPlaces finalPlaces,
  List<Bounty> bounties
) {
}
