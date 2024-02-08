package by.mrrockka.domain.player;

import by.mrrockka.domain.payments.Payments;
import lombok.Builder;

import java.util.Objects;

@Builder
public record Player(Payments payments, Person person) {

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Player player)) return false;
    return Objects.equals(person, player.person);
  }

  @Override
  public int hashCode() {
    return Objects.hash(person);
  }
}
