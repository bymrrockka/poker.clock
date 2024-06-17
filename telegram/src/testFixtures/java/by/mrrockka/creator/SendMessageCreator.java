package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SendMessageCreator {

  public static final Long CHAT_ID = 1 + Double.valueOf(Math.random() * 10).longValue();
  public static final String TEXT = FakerProvider.faker().backToTheFuture().quote();

  public static SendMessage api() {
    return api(null);
  }

  public static SendMessage api(final Consumer<SendMessage.SendMessageBuilder> sendMessageBuilderConsumer) {
    final var sendMessageBuilder = SendMessage.builder()
      .chatId(CHAT_ID)
      .text(TEXT);
    if (nonNull(sendMessageBuilderConsumer)) {
      sendMessageBuilderConsumer.accept(sendMessageBuilder);
    }
    return sendMessageBuilder.build();
  }
}
