package by.mrrockka.mapper;

import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UserCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeMentionUtilTest {
  private static final String TEXT = """
    /command
    @guys
    @julius
    %s
    @anybody
    """;

  private static Stream<Arguments> validMeMentions() {
    return Stream.of(
      Arguments.of("@me"),
      Arguments.of("@me "),
      Arguments.of("@me\n"),
      Arguments.of("@me\t"),
      Arguments.of("@me\r")
    );
  }

  @ParameterizedTest
  @MethodSource("validMeMentions")
  void givenMeUsername_whenReplaceUtilExecuted_shouldReplaceWithUsernameFromMessage(final String mention) {
    final var user = UserCreator.user();
    final var message = MessageCreator.message(builder -> {
      builder.setFrom(user);
      builder.setText(TEXT.formatted(mention));
    });

    assertThat(MeMentionUtil.hasMeMention(message)).isTrue();
    assertThat(MeMentionUtil.replaceMeMention(message))
      .contains(user.getUserName());
  }

  private static Stream<Arguments> invalidMeMentions() {
    return Stream.of(
      Arguments.of("@mer"),
      Arguments.of("@measd "),
      Arguments.of("@meoim\n"),
      Arguments.of("@mee\t"),
      Arguments.of("@meeem\r")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidMeMentions")
  void givenUsernamesStartingFromMe_whenReplaceUtilExecuted_shouldKeepSameUsername(final String mention) {
    final var message = MessageCreator.message(builder -> {
      builder.setFrom(UserCreator.user());
      builder.setText(TEXT.formatted(mention));
    });

    assertThat(MeMentionUtil.hasMeMention(message)).isFalse();
    assertThat(MeMentionUtil.replaceMeMention(message))
      .contains(mention);
  }

}