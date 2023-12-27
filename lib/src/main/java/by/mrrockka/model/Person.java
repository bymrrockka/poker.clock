package by.mrrockka.model;

import lombok.Builder;

@Builder
public record Person(String firstName, String lastName, String telegram, String phone) {

  public String fullname(){
    return firstName + " " + lastName ;
  }

  @Override
  public String toString() {
    return firstName + " " + lastName +
      ", telegram='" + telegram + '\'' +
      ", phone='" + phone + '\'';
  }
}
