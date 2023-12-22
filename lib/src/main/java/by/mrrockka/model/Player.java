package by.mrrockka.model;

import lombok.Builder;

@Builder
public record Player(Payments payments, int position, Person person) implements Comparable<Player>{
  @Override
  public int compareTo(Player pl) {
    return pl.payments().total().compareTo(this.payments().total());
  }
}
