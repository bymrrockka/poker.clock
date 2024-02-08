package by.mrrockka.domain.finaleplaces;

import by.mrrockka.domain.player.Person;

import java.util.List;
import java.util.Optional;

public record FinalePlaces(List<FinalPlace> finalePlaces) {

  public Optional<Person> getByPosition(int position) {
    return finalePlaces().stream()
      .filter(finalPlace -> finalPlace.position() == position)
      .map(FinalPlace::person)
      .findFirst();
  }

}
