package by.mrrockka.creator;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserCreator {

  private static final Faker FAKER = new Faker();
  public static final String USER_NAME = FAKER.name().username().replaceAll("\\.", "_");

  public static User user() {
    return user((Consumer<User>) null);
  }

  public static User user(final String username) {
    return user(user -> user.setUserName(username));
  }

  public static User user(final Consumer<User> userConsumer) {
    final var user = new User();
    user.setUserName(USER_NAME);

    if (nonNull(userConsumer)) {
      userConsumer.accept(user);
    }

    return user;
  }
}
