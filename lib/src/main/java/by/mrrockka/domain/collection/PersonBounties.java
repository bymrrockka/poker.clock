package by.mrrockka.domain.collection;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Person;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PersonBounties(@NonNull Person person, @NonNull List<Bounty> bounties) {

  public List<Bounty> taken() {
    return bounties().stream()
      .filter(bounty -> bounty.to().equals(person))
      .toList();
  }

  public List<Bounty> given() {
    return bounties().stream()
      .filter(bounty -> bounty.from().equals(person))
      .toList();
  }

  public BigDecimal totalTaken() {
    return taken().stream()
      .map(Bounty::amount)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }

  public BigDecimal totalGiven() {
    return given().stream()
      .map(Bounty::amount)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);
  }
}
