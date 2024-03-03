package by.mrrockka.repo.person;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramPersonEntity extends PersonEntity {

  @NonNull
  Long chatId;
  @NonNull
  String telegram;

  @Builder(builderMethodName = "telegramPersonBuilder")
  public TelegramPersonEntity(@NonNull UUID id, String firstname, String lastname, @NonNull Long chatId,
                              @NonNull String telegram) {
    super(id, firstname, lastname);
    this.chatId = chatId;
    this.telegram = telegram;
  }
}
