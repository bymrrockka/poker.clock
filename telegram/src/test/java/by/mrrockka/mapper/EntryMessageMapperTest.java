package by.mrrockka.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

class EntryMessageMapperTest {

  private final EntryMessageMapper entryMessageMapper = new EntryMessageMapper();


  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @king 60", BigDecimal.valueOf(60)),
      Arguments.of("/entry @king", null),
      Arguments.of("/reentry @king 60", BigDecimal.valueOf(60)),
      Arguments.of("/reentry @king", null)
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenEntryMessage_whenMapAttempt_shouldParseToPair(String command, BigDecimal amount) {
    final var actual = entryMessageMapper.map(command);
    assertThat(actual.getKey()).isEqualTo("king");
    if (nonNull(amount)) {
      assertThat(actual.getValue()).contains(amount);
    } else {
      assertThat(actual.getValue()).isEmpty();
    }
  }
}