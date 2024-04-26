package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersonCreator {

  private final static Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final String FIRSTNAME = FAKER.name().firstName();
  public static final String LASTNAME = FAKER.name().lastName();
  public static final String NICKNAME = FAKER.name().username().replaceAll("\\.", "_");

  public static PersonEntity entity() {
    return entity(null);
  }

  public static PersonEntity entity(Consumer<PersonEntity.PersonEntityBuilder> builderConsumer) {
    final var personEntityBuilder = PersonEntity.personBuilder()
      .id(ID)
      .firstname(FIRSTNAME)
      .lastname(LASTNAME)
      .nickname(NICKNAME);

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }

  public static Person domain() {
    return domain((Consumer<Person.PersonBuilder>) null);
  }

  public static Person domain(final String nickname) {
    return domain(builder -> builder.nickname(nickname));
  }

  public static Person domainRandom() {
    return domain(builder -> builder
      .id(UUID.randomUUID())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName())
      .nickname(FAKER.name().username().replaceAll("\\.", "_")));
  }

  public static Person domain(Consumer<Person.PersonBuilder> builderConsumer) {
    final var personEntityBuilder = Person.personBuilder()
      .id(ID)
      .firstname(FIRSTNAME)
      .lastname(LASTNAME)
      .nickname(NICKNAME);

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }
}
