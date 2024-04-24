package by.mrrockka.validation.finaleplaces;

import by.mrrockka.domain.TelegramPerson;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FinalePlacesValidator {

  public void validatePlaces(final Map<Integer, TelegramPerson> places) {
    if (places.isEmpty()) {
      throw new FinalePlacesCannotBeEmptyException();
    }
  }
}
