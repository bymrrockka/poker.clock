package by.mrrockka.route.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCreator {

  public static Update update(Message message) {
    return update(update -> update.setMessage(message));
  }

  public static Update update(Consumer<Update> updateConsumer) {
    final var update = new Update();

    if (nonNull(updateConsumer)) {
      updateConsumer.accept(update);
    }

    return update;
  }

  public static Message message(String text) {
    return message((message) -> message.setText(text));
  }

  public static Message message(Consumer<Message> messageConsumer) {
    final var entity = new MessageEntity();
    entity.setOffset(0);
    entity.setType(EntityType.BOTCOMMAND);

    final var chat = new Chat();
    chat.setId(Double.valueOf(Math.random() * 10).longValue());
    final var message = new Message();
    message.setChat(chat);
    message.setEntities(List.of(entity));

    if (nonNull(messageConsumer)) {
      messageConsumer.accept(message);
    }

    return message;
  }


}
