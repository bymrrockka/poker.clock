package by.mrrockka.creator;

import by.mrrockka.FakerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageCreator {

  public static final Instant MESSAGE_TIMESTAMP = Instant.now();
  public static final Integer MESSAGE_ID = Double.valueOf(100 + Math.random() * 100).intValue();
  public static final String MESSAGE_TEXT = FakerProvider.faker().chuckNorris().fact();

  public static Message message() {
    return message((Consumer<Message>) null);
  }

  public static Message message(String text) {
    return message((message) -> message.setText(text));
  }

  public static Message message(Consumer<Message> messageConsumer) {
    final var entity = new MessageEntity();
    entity.setOffset(0);
    entity.setType(EntityType.BOTCOMMAND);

    final var message = new Message();
    message.setMessageId(MESSAGE_ID);
    message.setText(MESSAGE_TEXT);
    message.setChat(ChatCreator.chat());
    message.setEntities(List.of(entity));
    message.setFrom(UserCreator.user());
    message.setDate((int) MESSAGE_TIMESTAMP.getEpochSecond());

    if (nonNull(messageConsumer)) {
      messageConsumer.accept(message);
    }

    return message;
  }

}
