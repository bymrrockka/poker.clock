package by.mrrockka.domain.finaleplaces;

import by.mrrockka.domain.Person;
import lombok.Builder;

@Builder
@Deprecated(forRemoval = true)
public record FinalPlace(Integer position, Person person) {
}
