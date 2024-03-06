package by.mrrockka.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Person {

  @NonNull
  UUID id;
  String firstname;
  String lastname;

//  todo: add nickname field and refactor

  @Builder(builderMethodName = "personBuilder")
  public Person(@NonNull final UUID id, final String firstname, final String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}
