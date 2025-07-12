package by.mrrockka.validation.collection;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;


@Deprecated(forRemoval = true)
class EntityCouldNotBeEmptyException extends BusinessException {
  EntityCouldNotBeEmptyException(@NonNull final String entity) {
    super("%s could not be empty".formatted(entity));
  }
}
