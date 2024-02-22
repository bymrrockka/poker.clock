package by.mrrockka.route;

import by.mrrockka.creator.MessageCreator;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static by.mrrockka.creator.UpdateCreator.update;
import static org.assertj.core.api.Assertions.assertThat;

class CalculatePaymentsCommandRouteTest {

  private final CalculatePaymentsCommandRoute calculatePaymentsRoute = new CalculatePaymentsCommandRoute();

  @Builder
  private record MessageArgument(String message, boolean result, boolean isCommand) {
  }

  private static Stream<Arguments> messages() {
    return Stream.of(
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate ")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate something")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate 123")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate asd123")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calcul ate asd")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/baower")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/baower calculate")
          .result(false)
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("messages")
  void givenUpdateContainsMessage_whenEqualsCalculate_thenShouldAssertTrue(MessageArgument arg) {
    final var update = update(MessageCreator.message(message -> message.setText(arg.message())));

    assertThat(calculatePaymentsRoute.isApplicable(update))
      .isEqualTo(arg.result());
  }


}