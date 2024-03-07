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
  public TelegramPerson(@NonNull final UUID id, final String firstname, final String lastname,
                        @NonNull final Long chatId, @NonNull final String telegram) {
    super(id, firstname, lastname);
    this.chatId = chatId;
    this.telegram = telegram;
  }
}
