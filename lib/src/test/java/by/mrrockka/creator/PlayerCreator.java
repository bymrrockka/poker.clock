package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.Player;
import by.mrrockka.domain.payments.Payments;
import com.github.javafaker.Faker;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PlayerCreator {

  private static final Faker FAKER = FakerProvider.faker();

  public static Player player() {
    return player(null);
  }

  public static Player player(final Consumer<Player.PlayerBuilder> playerBuilderConsumer) {
    final var playerBuilder = Player.builder()
      .person(person())
      .payments(new Payments(List.of()));


    if (nonNull(playerBuilderConsumer))
      playerBuilderConsumer.accept(playerBuilder);

    return playerBuilder.build();
  }

  private static Person person() {
    return Person.builder()
      .id(UUID.randomUUID())
      .chatId(FAKER.random().hex())
      .telegram(FAKER.funnyName().name())
      .build();
  }
}
