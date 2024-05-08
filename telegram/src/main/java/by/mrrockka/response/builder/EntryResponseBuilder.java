package by.mrrockka.response.builder;

import by.mrrockka.domain.TelegramPerson;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static by.mrrockka.response.builder.TextContants.*;

@Component
public class EntryResponseBuilder {

  public String response(final List<TelegramPerson> persons, final BigDecimal amount) {
    final var strBuilder = new StringBuilder("Entries:");

    persons.forEach(person -> strBuilder
      .append(NL)
      .append(MINUS)
      .append(AT).append(person.getNickname())
      .append(POINTER)
      .append(amount)
    );

    return strBuilder.append(NL).toString();
  }
}
