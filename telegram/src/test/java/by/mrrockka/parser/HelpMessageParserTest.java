package by.mrrockka.parser;

import by.mrrockka.creator.MessageMetadataCreator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HelpMessageParserTest {

  private final HelpMessageParser mapper = new HelpMessageParser();

  private static Stream<Arguments> helpMessage() {
    return Stream.of(
      Arguments.of("/help", null),
      Arguments.of("/help command", "command")
    );
  }

  @ParameterizedTest
  @MethodSource("helpMessage")
  void givenMetadataWithHelpText_whenParse_thenShouldReturnOptionalWithCommand(final String text,
                                                                               final String expected) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));

    if (nonNull(expected)) {
      assertThat(mapper.parse(metadata)).contains(expected);
    } else {
      assertThat(mapper.parse(metadata)).isEmpty();
    }
  }

  private static Stream<Arguments> invalidMessage() {
    return Stream.of(
      Arguments.of("/hel"),
      Arguments.of("/held command")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidMessage")
  void givenMetadataWithInvalidText_whenParse_thenShouldThrowException(final String text) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));

    assertThatCode(() -> mapper.parse(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

}