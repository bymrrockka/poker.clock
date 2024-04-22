package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.domain.TelegramPerson;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TelegramPersonCreator {

  private static final Faker FAKER = FakerProvider.faker();
  public static final UUID ID = UUID.randomUUID();
  public static final Long CHAT_ID = FAKER.random().nextLong();
  public static final String FIRSTNAME = FAKER.name().firstName();
  public static final String LASTNAME = FAKER.name().lastName();
  public static final String NICKNAME = FAKER.name().username().replaceAll("\\.", "_");

  public static TelegramPerson domain() {
    return domain((Consumer<TelegramPerson.TelegramPersonBuilder>) null);
  }

  public static TelegramPerson domain(final String nickname) {
    return domain(builder -> builder.nickname(nickname));
  }

  public static TelegramPerson domainRandom() {
    return domain(builder -> builder
      .id(UUID.randomUUID())
      .chatId(FAKER.random().nextLong())
      .firstname(FAKER.name().firstName())
      .lastname(FAKER.name().lastName())
      .nickname(FAKER.name().username().replaceAll("\\.", "_")));
  }

  public static TelegramPerson domain(final Consumer<TelegramPerson.TelegramPersonBuilder> builderConsumer) {
    final var personEntityBuilder = TelegramPerson.telegramPersonBuilder()
      .id(ID)
      .chatId(CHAT_ID)
      .firstname(FIRSTNAME)
      .lastname(LASTNAME)
      .nickname(NICKNAME);

    if (nonNull(builderConsumer))
      builderConsumer.accept(personEntityBuilder);

    return personEntityBuilder.build();
  }
}
