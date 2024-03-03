package by.mrrockka.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramPerson extends Person {

  @NonNull
  Long chatId;
  @NonNull
  String telegram;

  @Builder(builderMethodName = "telegramPersonBuilder")
  public TelegramPerson(@NonNull UUID id, String firstname, String lastname, @NonNull Long chatId,
                        @NonNull String telegram) {
    super(id, firstname, lastname);
    this.chatId = chatId;
    this.telegram = telegram;
  }
}
