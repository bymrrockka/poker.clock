package by.mrrockka.creator;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;


@Deprecated(forRemoval = true, since = "1.1")
public class PlayerCreator {

  public static Player player() {
    return player(null);
  }

  public static Player player(final Consumer<Player.PlayerBuilder> playerBuilderConsumer) {
    final var playerBuilder = Player.builder()
      .person(PersonCreator.domain())
      .entries(new Entries(PersonCreator.domain(), List.of(BigDecimal.ONE)));

    if (nonNull(playerBuilderConsumer))
      playerBuilderConsumer.accept(playerBuilder);

    return playerBuilder.build();
  }

}
