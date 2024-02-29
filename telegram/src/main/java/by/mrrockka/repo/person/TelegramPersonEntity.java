package by.mrrockka.repo.person;


import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramPersonEntity extends PersonEntity {

  @NonNull
  Long chatId;
  @NonNull
  String telegram;
}
