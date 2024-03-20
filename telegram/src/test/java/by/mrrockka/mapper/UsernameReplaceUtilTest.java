package by.mrrockka.mapper;

import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UserCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class UsernameReplaceUtilTest {

  private static Stream<Arguments> usernamesToReplace() {
    return Stream.of(
      Arguments.of("@me"),
      Arguments.of("@me "),
      Arguments.of("@me\n"),
      Arguments.of("@me\t"),
      Arguments.of("@me\r")
    );
  }

  @ParameterizedTest
  @MethodSource("usernamesToReplace")
  void givenMeUsername_whenReplaceUtilExecuted_shouldReplaceWithUsernameFromMessage(final String me) {
    final var user = UserCreator.user();
    final var message = MessageCreator.message(builder -> {
      builder.setFrom(user);
      builder.setText(me);
    });

    assertThat(UsernameReplaceUtil.replaceUsername(message))
      .contains(user.getUserName());
  }

  private static Stream<Arguments> usernamesToKeep() {
    return Stream.of(
      Arguments.of("@mer"),
      Arguments.of("@measd "),
      Arguments.of("@meoim\n"),
      Arguments.of("@mee\t"),
      Arguments.of("@meeem\r")
    );
  }

  @ParameterizedTest
  @MethodSource("usernamesToKeep")
  void givenUsernamesStartingFromMe_whenReplaceUtilExecuted_shouldKeepSameUsername(final String me) {
    final var message = MessageCreator.message(builder -> {
      builder.setFrom(UserCreator.user());
      builder.setText(me);
    });

    assertThat(UsernameReplaceUtil.replaceUsername(message))
      .isEqualTo(me);
  }

}