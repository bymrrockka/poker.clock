package by.mrrockka.domain;

import by.mrrockka.domain.game.Game;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramGame extends Game {

  @NonNull
  Long chatId;

}
