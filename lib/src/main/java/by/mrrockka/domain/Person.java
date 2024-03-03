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

  @Builder(builderMethodName = "personBuilder")
  public Person(@NonNull UUID id, String firstname, String lastname) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}
