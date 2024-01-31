package by.mrrockka.domain;

import lombok.Builder;

import static java.util.Objects.nonNull;

@Builder
public record Person(String firstName, String lastName, String telegram) {

  public String fullname() {
    return firstName + ' ' + lastName;
  }

  @Override
  public String toString() {
    final var output = new StringBuilder();
    output.append('{');
    if (nonNull(firstName) && nonNull(lastName)) {
      output.append(firstName).append(' ').append(lastName).append(',');
    } else if (nonNull(firstName)) {
      output.append(firstName).append(',');
    }

    if (nonNull(telegram)) {
      output.append("telegram=").append(telegram);
    }

    return output.append('}').toString();
  }
}
