package by.mrrockka.parser;

import by.mrrockka.domain.MessageMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Deprecated
public class HelpMessageParser {

  private static final String HELP_REGEX = "^/help([ ]*)(.*)$";

  private static final int COMMAND_GROUP = 2;
  private static final String ERROR_MESSAGE = "/help (command)";

  public Optional<String> parse(final MessageMetadata metadata) {
    final var str = metadata.getText().toLowerCase().strip();
    final var matcher = Pattern.compile(HELP_REGEX).matcher(str);
    if (matcher.matches()) {
      final var command = matcher.group(COMMAND_GROUP);
      return StringUtils.isNoneBlank(command) ? Optional.of(command) : Optional.empty();
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

}
