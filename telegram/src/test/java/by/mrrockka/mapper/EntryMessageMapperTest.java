package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntryMessageMapperTest {

  private final EntryMessageMapper entryMessageMapper = new EntryMessageMapper();


  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @kinger 60", BigDecimal.valueOf(60)),
      Arguments.of("/entry @kinger", null),
      Arguments.of("/reentry @kinger 60", BigDecimal.valueOf(60)),
      Arguments.of("/reentry @kinger", null)
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenEntryMessage_whenMapAttempt_shouldParseToPair(final String command, final BigDecimal amount) {
    final var actual = entryMessageMapper.map(command);
    assertThat(actual.getKey()).isEqualTo("kinger");
    if (nonNull(amount)) {
      assertThat(actual.getValue()).contains(amount);
    } else {
      assertThat(actual.getValue()).isEmpty();
    }
  }


  private static Stream<Arguments> invalidEntryMessage() {
    return Stream.of(
      Arguments.of("/entry 60 @kinger"),
      Arguments.of("/entry@kinger"),
      Arguments.of("/entry"),
      Arguments.of("@kinger/entry"),
      Arguments.of("/reentry 60 @kinger"),
      Arguments.of("/reentry@kinger"),
      Arguments.of("@kinger/reentry"),
      Arguments.of("/reentry")
    );
  }

  @ParameterizedTest
  @MethodSource("invalidEntryMessage")
  void givenInvalidEntryMessage_whenMapAttempt_shouldThrowException(final String message) {
    assertThatThrownBy(() -> entryMessageMapper.map(message))
      .isInstanceOf(InvalidMessageFormatException.class);
  }
}