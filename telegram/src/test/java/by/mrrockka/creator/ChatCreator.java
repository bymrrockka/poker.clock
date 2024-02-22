package by.mrrockka.creator;

import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class ChatCreator {

  public static Chat chat() {
    return chat(Double.valueOf(Math.random() * 10).longValue());
  }

  public static Chat chat(Long chatId) {
    return chat(chat -> chat.setId(chatId));
  }

  public static Chat chat(Consumer<Chat> chatConsumer) {
    final var chat = new Chat();

    if (nonNull(chatConsumer)) {
      chatConsumer.accept(chat);
    }

    return chat;
  }

}
