package by.mrrockka.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatCreator {

  public static final Long CHAT_ID = randomChatId();

  public static Chat chat() {
    return chat(CHAT_ID);
  }

  public static Chat chat(final Long chatId) {
    return chat(chat -> chat.setId(chatId));
  }

  public static Chat chat(final Consumer<Chat> chatConsumer) {
    final var chat = new Chat();

    if (nonNull(chatConsumer)) {
      chatConsumer.accept(chat);
    }

    return chat;
  }

  public static Long randomChatId() {
    return 1 + Double.valueOf(Math.random() * 10).longValue();
  }

}
