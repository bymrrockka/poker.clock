package by.mrrockka.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.shaded.org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FinalePlacesMessageMapperTest {

  private final FinalePlacesMessageMapper finalePlacesMessageMapper = new FinalePlacesMessageMapper();

  private static Stream<Arguments> finalePlacesMessage() {
    return Stream.of(
      Arguments.of(
        """
          /finaleplaces
          1 @mrrockka
          2 @ararat
          3 @andrei
          """
      ),
      Arguments.of(
        """
          /finaleplaces
          1. @mrrockka
          2. @ararat
          3. @andrei
          """
      ),
      Arguments.of(
        """
          /finaleplaces
          1 - @mrrockka
          2 -@ararat
          3-@andrei
          """
      ),
      Arguments.of(
        """
          /finaleplaces
          1 @mrrockka, 2. @ararat,3- @andrei
          """
      ),
      Arguments.of(
        """
          /finaleplaces
          1= @mrrockka, 2. @ararat,
          3: @andrei
          """
      )
    );
  }

  @ParameterizedTest
  @MethodSource("finalePlacesMessage")
  void givenFinalePlacesCommand_whenAttemptToMap_shouldReturnPositionAndTelegram(String command) {
    final var expected = List.of(
      Pair.of(1, "mrrockka"),
      Pair.of(2, "ararat"),
      Pair.of(3, "andrei")
    );

    assertThat(finalePlacesMessageMapper.map(command))
      .isEqualTo(expected);
  }
}