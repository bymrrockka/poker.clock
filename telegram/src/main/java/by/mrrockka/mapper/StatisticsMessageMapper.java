package by.mrrockka.mapper;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.mapper.exception.InvalidMessageFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StatisticsMessageMapper {

  private static final String STATISTICS_REGEX = "^/(game|my)_stats";
  private static final int TYPE_GROUP = 1;
  private static final String ERROR_MESSAGE = "/(game|my)_stats";

  public StatisticsCommand map(final MessageMetadata messageMetadata) {
    final var str = messageMetadata.command().toLowerCase().strip();
    final var matcher = Pattern.compile(STATISTICS_REGEX).matcher(str);

    if (!matcher.matches()) {
      throw new InvalidMessageFormatException(ERROR_MESSAGE);
    }

    return StatisticsCommand.builder()
      .type(isType(matcher.group(TYPE_GROUP), messageMetadata))
      .metadata(messageMetadata)
      .build();
  }

  private StatisticsType isType(final String type, final MessageMetadata messageMetadata) {
    if ("my".equals(type) && messageMetadata.optReplyTo().isPresent()) {
      return StatisticsType.PLAYER_IN_GAME;
    } else if ("my".equals(type)) {
      return StatisticsType.PERSON_GLOBAL;
    } else {
      return StatisticsType.GAME;
    }
  }

}
