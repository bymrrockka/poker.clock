package by.mrrockka.domain;

import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Builder
public record Person(UUID id, String chatId, String firstname, String lastname, String telegram) {

  @Override
  public String toString() {
    final var output = new StringBuilder();
    output.append('{');
    if (nonNull(firstname) && nonNull(lastname)) {
      output.append(firstname).append(' ').append(lastname).append(',');
    } else if (nonNull(firstname)) {
      output.append(firstname).append(',');
    }

    if (nonNull(telegram)) {
      output.append("telegram=").append(telegram);
    }

    return output.append('}').toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Person person))
      return false;
    return Objects.equals(id, person.id) &&
      Objects.equals(chatId, person.chatId) &&
      Objects.equals(telegram, person.telegram);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, chatId, telegram);
  }
}
