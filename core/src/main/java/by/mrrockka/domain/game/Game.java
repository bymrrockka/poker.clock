package by.mrrockka.domain.game;

import by.mrrockka.domain.collection.PersonEntries;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Game {

  @NonNull
  protected UUID id;
  @NonNull
  protected BigDecimal buyIn;
  @NonNull
  protected BigDecimal stack;
  protected Instant finishedAt;
  protected List<PersonEntries> entries;

  protected Game(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                 @NonNull final BigDecimal stack, final Instant finishedAt, final List<PersonEntries> entries) {
    this.id = id;
    this.buyIn = buyIn;
    this.stack = stack;
    this.finishedAt = finishedAt;
    this.entries = entries;
  }

  public <T extends Game> T asType(final Class<T> clazz) {
    return clazz.cast(this);
  }

  public boolean isType(final Class<? extends Game> clazz) {
    return this.getClass().equals(clazz);
  }

  public UUID getId() {return id;}

  public BigDecimal getBuyIn() {return buyIn;}

  public BigDecimal getStack() {return stack;}

  public Instant getFinishedAt() {return finishedAt;}

  public List<PersonEntries> getEntries() {return entries;}


}
