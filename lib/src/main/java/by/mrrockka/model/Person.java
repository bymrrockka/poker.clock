package by.mrrockka.model;

import lombok.Builder;

@Builder
public record Person(String firstName, String lastName, String telegram, String phone) {
}
