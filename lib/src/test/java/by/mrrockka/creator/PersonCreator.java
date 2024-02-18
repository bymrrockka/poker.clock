package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import com.github.javafaker.Faker;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PersonCreator {

  private final static Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final String FIRSTNAME = FAKER.name().firstName();
  public static final String LASTNAME = FAKER.name().lastName();

  public static PersonEntity entity() {
    return entity(null);
  }

  public static PersonEntity entityRandom() {
    return entity(builder -> builder
      .id(UUID.randomUUID())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName()));
  }

  public static PersonEntity entity(Consumer<PersonEntity.PersonEntityBuilder> builderConsumer) {
    final var personEntityBuilder = PersonEntity.builder()
      .id(ID)
      .firstname(FIRSTNAME)
      .lastname(LASTNAME);

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }

  public static Person domain() {
    return domain(null);
  }

  public static Person domainRandom() {
    return domain(builder -> builder
      .id(UUID.randomUUID())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName()));
  }

  public static Person domain(Consumer<Person.PersonBuilder> builderConsumer) {
    final var personEntityBuilder = Person.builder()
      .id(ID)
      .firstname(FIRSTNAME)
      .lastname(LASTNAME);

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }
}
