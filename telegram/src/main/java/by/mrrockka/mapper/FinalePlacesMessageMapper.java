package by.mrrockka.mapper;

import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.mapper.CommandRegexConstants.DELIMITER_REGEX;
import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
public class FinalePlacesMessageMapper {

  private static final String FINALE_PLACES_REGEX = "^(\\d)%s%s$".formatted(DELIMITER_REGEX, TELEGRAM_NAME_REGEX);

  public List<Pair<Integer, String>> map(final String command) {
    final var strings = command.toLowerCase().strip().replaceAll("/finaleplaces([ \n]*)", "").split("[\n,;]");
    final var placesPattern = Pattern.compile(FINALE_PLACES_REGEX);
    final var result = Arrays.stream(strings)
      .map(String::strip)
      .map(placesPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> Pair.of(Integer.parseInt(matcher.group(1)), matcher.group(3)))
      .collect(Collectors.toList());

    if (result.isEmpty()) {
      throw new InvalidMessageFormatException(FINALE_PLACES_REGEX);
    }

    return result;
  }
}
