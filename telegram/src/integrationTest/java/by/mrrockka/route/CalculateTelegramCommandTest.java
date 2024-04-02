package by.mrrockka.route;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import lombok.Builder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class CalculateTelegramCommandTest {

  @Autowired
  private CalculateTelegramCommand calculatePaymentsRoute;

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
          .message("/calculate\n")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate\t")
          .result(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate something")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate 123")
          .result(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate asd123")
          .result(false)
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
    final var update = UpdateCreator.update(MessageCreator.message(message -> message.setText(arg.message())));

    assertThat(calculatePaymentsRoute.isApplicable(update))
      .isEqualTo(arg.result());
  }
}