package by.mrrockka.route.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendCreator {

  public static SendMessage sendMessage(Consumer<SendMessage.SendMessageBuilder> sendMessageBuilderConsumer) {
    final var sendMessageBuilder = SendMessage.builder();
    if (nonNull(sendMessageBuilderConsumer)) {
      sendMessageBuilderConsumer.accept(sendMessageBuilder);
    }
    return sendMessageBuilder.build();
  }
}
