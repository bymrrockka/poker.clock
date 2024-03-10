package by.mrrockka.service.exception;

import by.mrrockka.exception.BusinessException;
import lombok.NonNull;

import static by.mrrockka.service.exception.TelegramErrorCodes.MODEL_IS_EMPTY;

public class ModelIsEmptyException extends BusinessException {
  public ModelIsEmptyException(@NonNull final String modelName) {
    super("%s is empty.".formatted(modelName), MODEL_IS_EMPTY);
  }
}
