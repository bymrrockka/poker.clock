package by.mrrockka.parser;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.parser.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class WithdrawalMessageParser {

  private final TelegramPersonMapper personMapper;

  private static final String WITHDRAWAL_REGEX = "^/withdrawal(( %s)+) ([\\d]+)$".formatted(TELEGRAM_NAME_REGEX);
  private static final int AMOUNT_GROUP = 4;
  private static final String ERROR_MESSAGE = "/withdrawal @nickname( @nickname) #amount";

  //  Map to be able to extend withdrawals to have different values for players
  public Map<TelegramPerson, BigDecimal> parse(final MessageMetadata metadata) {
    final var command = metadata.text().toLowerCase().strip();
    final var chatId = metadata.chatId();
    final var matcher = Pattern.compile(WITHDRAWAL_REGEX).matcher(command);
    if (matcher.matches()) {
      final var amount = new BigDecimal(matcher.group(AMOUNT_GROUP));

      return metadata.mentions()
        .map(entity -> personMapper.mapMessageToTelegramPerson(entity, chatId))
        .collect(Collectors.toMap(Function.identity(), p -> amount));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

}
