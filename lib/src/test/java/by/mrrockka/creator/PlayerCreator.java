package by.mrrockka.creator;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.payments.Payments;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PlayerCreator {

  public static Player player() {
    return player(null);
  }

  public static Player player(final Consumer<Player.PlayerBuilder> playerBuilderConsumer) {
    final var playerBuilder = Player.builder()
      .person(PersonCreator.domain())
      .payments(new Payments(List.of()));


    if (nonNull(playerBuilderConsumer))
      playerBuilderConsumer.accept(playerBuilder);

    return playerBuilder.build();
  }

}
