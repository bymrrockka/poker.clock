package by.mrrockka.domain.finaleplaces;

import by.mrrockka.domain.Person;

import java.util.List;
import java.util.Optional;

public record FinalePlaces(List<FinalPlace> finalPlaces) {

  public Optional<Person> getByPosition(int position) {
    return finalPlaces().stream()
      .filter(finalPlace -> finalPlace.position() == position)
      .map(FinalPlace::person)
      .findFirst();
  }

}
