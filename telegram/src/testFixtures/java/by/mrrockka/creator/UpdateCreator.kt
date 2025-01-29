package by.mrrockka.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateCreator {

  public static Update update(final Message message) {
    return update(update -> update.setMessage(message));
  }

  public static Update update(final Consumer<Update> updateConsumer) {
    final var update = new Update();

    if (nonNull(updateConsumer)) {
      updateConsumer.accept(update);
    }

    return update;
  }


}
