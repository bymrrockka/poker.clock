package by.mrrockka.domain.finaleplaces;

import by.mrrockka.domain.Person;
import lombok.Builder;

@Builder
public record FinalPlace(Integer position, Person person) {
}
