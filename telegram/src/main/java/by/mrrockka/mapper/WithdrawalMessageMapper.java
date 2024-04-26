package by.mrrockka.mapper;

import by.mrrockka.domain.MessageEntityType;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import by.mrrockka.mapper.person.TelegramPersonMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static by.mrrockka.mapper.CommandRegexConstants.TELEGRAM_NAME_REGEX;

@Component
@RequiredArgsConstructor
public class WithdrawalMessageMapper {

  private final TelegramPersonMapper personMapper;

  @Value("${telegrambots.name}")
  @Setter
  private String botName;

  private static final String WITHDRAWAL_REGEX = "^/withdrawal(( %s)+) ([\\d]+)$".formatted(TELEGRAM_NAME_REGEX);
  private static final int MENTION_GROUP = 2;
  private static final int AMOUNT_GROUP = 4;
  private static final String ERROR_MESSAGE = "/withdrawal @nickname( @nickname) #amount";

  @Deprecated(since = "1.1.0", forRemoval = true)
  public Pair<String, BigDecimal> map(final String command) {
    final var str = command.toLowerCase().strip();
    final var matcher = Pattern.compile(WITHDRAWAL_REGEX).matcher(str);
    if (matcher.matches()) {
      return Pair.of(matcher.group(MENTION_GROUP), new BigDecimal(matcher.group(AMOUNT_GROUP)));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

  //  Map to be able to extend withdrawals to have different values for players
  public Map<TelegramPerson, BigDecimal> map(final MessageMetadata metadata) {
    final var command = metadata.command().toLowerCase().strip();
    final var chatId = metadata.chatId();
    final var matcher = Pattern.compile(WITHDRAWAL_REGEX).matcher(command);
    if (matcher.matches()) {
      final var amount = new BigDecimal(matcher.group(AMOUNT_GROUP));

      return metadata.entities().stream()
        .filter(entity -> entity.type().equals(MessageEntityType.MENTION))
        .filter(entity -> !entity.text().contains(botName))
        .map(entity -> personMapper.mapMessageToTelegram(entity, chatId))
        .collect(Collectors.toMap(Function.identity(), p -> amount));
    }

    throw new InvalidMessageFormatException(ERROR_MESSAGE);
  }

}
