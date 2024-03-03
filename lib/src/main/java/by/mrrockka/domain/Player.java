package by.mrrockka.domain;

import by.mrrockka.domain.payments.Entries;
import lombok.Builder;

import java.util.Objects;

@Builder
public record Player(Person person, Entries entries) {

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
