package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import by.mrrockka.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageCreator {

  public static final Instant MESSAGE_TIMESTAMP = Instant.now();
  public static final Integer MESSAGE_ID = Random.messageId();
  public static final String MESSAGE_TEXT = FakerProvider.faker().chuckNorris().fact();

  public static Message message() {
    return message((Consumer<Message>) null);
  }

  public static Message message(final String text) {
    return message((message) -> {
      message.setText(text);
      message.setEntities(List.of(MessageEntityCreator.apiCommand(text, text)));
    });
  }

  public static Message message(final Consumer<Message> messageConsumer) {
    final var message = new Message();
    message.setMessageId(MESSAGE_ID);
    message.setText(MESSAGE_TEXT);
    message.setChat(ChatCreator.chat());
    message.setEntities(List.of(MessageEntityCreator.apiEntity()));
    message.setFrom(UserCreator.user());
    message.setDate((int) MESSAGE_TIMESTAMP.getEpochSecond());

    if (nonNull(messageConsumer)) {
      messageConsumer.accept(message);
    }

    return message;
  }

}
