package by.mrrockka.repo.entities;

import java.util.UUID;

public record PersonEntity(
  UUID id,
  String chatId,
  String telegram,
  String firstname,
  String lastname
) {
}
