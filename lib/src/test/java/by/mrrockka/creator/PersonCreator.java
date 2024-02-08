package by.mrrockka.creator;

import by.mrrockka.domain.player.Person;
import by.mrrockka.repo.person.PersonEntity;
import com.github.javafaker.Faker;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PersonCreator {

  private final static Faker FAKER = new Faker();

  public static PersonEntity entity() {
    return entity(null);
  }

  public static PersonEntity entity(Consumer<PersonEntity.PersonEntityBuilder> builderConsumer) {
    final var personEntityBuilder = PersonEntity.builder()
      .id(UUID.randomUUID())
      .chatId(FAKER.random().hex())
      .telegram(FAKER.funnyName().name())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName());

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }

  public static Person domain() {
    return domain(null);
  }

  public static Person domain(Consumer<Person.PersonBuilder> builderConsumer) {
    final var personEntityBuilder = Person.builder()
      .id(UUID.randomUUID())
      .chatId(FAKER.random().hex())
      .telegram(FAKER.funnyName().name())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName());

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }
}
