package by.mrrockka.domain.game;

import by.mrrockka.domain.collection.PersonEntries;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@ToString
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Game {

  @NonNull
  protected UUID id;
  @NonNull
  protected BigDecimal buyIn;
  @NonNull
  protected BigDecimal stack;
  protected List<PersonEntries> entries;

  protected Game(@NonNull final UUID id, @NonNull final BigDecimal buyIn,
                 @NonNull final BigDecimal stack, final List<PersonEntries> entries) {
    this.id = id;
    this.buyIn = buyIn;
    this.stack = stack;
    this.entries = entries;
  }

  public <T extends Game> T asType(final Class<T> clazz) {
    return clazz.cast(this);
  }

  public boolean isType(final Class<? extends Game> clazz) {
    return this.getClass().equals(clazz);
  }

}
