package by.mrrockka.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FinalePlacesMessageMapper {

  public List<Pair<Integer, String>> map(String command) {
    final var strings = command.toLowerCase().strip().split("[\n,;]");
    final var placesPattern = Pattern.compile("^(\\d)([. :\\-=]{1,3})@([A-z]+)$");
    return Arrays.stream(strings)
      .map(String::strip)
      .map(placesPattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> Pair.of(Integer.parseInt(matcher.group(1)), matcher.group(3)))
      .collect(Collectors.toList());
  }
}
