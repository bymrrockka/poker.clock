package by.mrrockka.repo.entities;

import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

@Builder
public record PersonEntity(
  @NonNull
  UUID id,
  @NonNull
  String chatId,
  @NonNull
  String telegram,
  String firstname,
  String lastname
) {
}
