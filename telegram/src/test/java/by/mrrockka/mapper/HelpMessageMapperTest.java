package by.mrrockka.mapper;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HelpMessageMapperTest {

  private final HelpMessageMapper mapper = new HelpMessageMapper();

  private static Stream<Arguments> helpMessage() {
    return Stream.of(
      Arguments.of("/help", null),
      Arguments.of("/help command", "command")
    );
  }

  @ParameterizedTest
  @MethodSource("helpMessage")
  void givenMetadataWithHelpText_whenMap_thenShouldReturnOptionalWithCommand(final String text, final String expected) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));

    if (nonNull(expected)) {
      assertThat(mapper.map(metadata)).contains(expected);
    } else {
      assertThat(mapper.map(metadata)).isEmpty();
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
  void givenMetadataWithInvalidText_whenMap_thenShouldThrowException(final String text) {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.text(text));

    assertThatCode(() -> mapper.map(metadata))
      .isInstanceOf(InvalidMessageFormatException.class);
  }

}