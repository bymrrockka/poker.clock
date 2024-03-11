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

  @Builder(builderMethodName = "telegramPersonBuilder")
  public TelegramPersonEntity(@NonNull final UUID id, final String firstname, final String lastname,
                              @NonNull final Long chatId, @NonNull final String nickname) {
    super(id, firstname, lastname, nickname);
    this.chatId = chatId;
  }
}
