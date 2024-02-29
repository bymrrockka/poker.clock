package by.mrrockka.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EntryMessageMapperTest {

  private final EntryMessageMapper entryMessageMapper = new EntryMessageMapper();


  private static Stream<Arguments> entryMessage() {
    return Stream.of(
      Arguments.of("/entry @king 60", Optional.of(BigDecimal.valueOf(60))),
      Arguments.of("/entry @king", Optional.<BigDecimal>empty())
    );
  }

  @ParameterizedTest
  @MethodSource("entryMessage")
  void givenEntryMessage_whenMapAttempt_shouldParseToPair(String command, Optional<BigDecimal> amount) {
    final var actual = entryMessageMapper.map(command);
    final var expected = Pair.of("king", amount);
    assertThat(actual).isEqualTo(expected);
  }
}