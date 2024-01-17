package by.mrrockka.route;

import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatePaymentsRouteTest {

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
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate")
          .result(false)
          .isCommand(false)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate ")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate something")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate 123")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calculate asd123")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/calcul ate asd")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/baower")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("")
          .result(false)
          .isCommand(true)
          .build()),
      Arguments.of(
        MessageArgument.builder()
          .message("/baower calculate")
          .result(false)
          .isCommand(true)
          .build())
    );
  }

  @ParameterizedTest
  @MethodSource("messages")
  void givenUpdateContainsMessage_whenEqualsCalculate_thenShouldAssertTrue(MessageArgument arg) {
    assertThat(calculatePaymentsRoute.isApplicable(update(arg.message(), arg.isCommand())))
      .isEqualTo(arg.result());
  }

  private Update update(String text, boolean isCommand) {
    final var entity = new MessageEntity();
    entity.setOffset(0);
    if (isCommand) {
      entity.setType(EntityType.BOTCOMMAND);
    }
    final var pinnedMessage = new Message();
    final var message = new Message(null, null, null, null, null, null, null, null, text, List.of(entity), null, null, null, null, null, null, null, null, null, null, pinnedMessage, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    final var update = new Update();
    update.setMessage(message);
    return update;
  }

}