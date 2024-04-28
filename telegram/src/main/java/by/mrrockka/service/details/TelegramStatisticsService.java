package by.mrrockka.service.details;

import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.StatisticsMessageMapper;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramStatisticsService {

  private final MessageMetadataMapper messageMetadataMapper;
  private final StatisticsMessageMapper statisticsMessageMapper;
  private final PersonMentionsValidator personMentionsValidator;
  private final PlayerInGameStatisticsService playerInGameStatisticsService;
  private final GameStatisticsService gameStatisticsService;

  public BotApiMethodMessage retrieveDetails(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    personMentionsValidator.validateMessageHasUserTextMention(messageMetadata);
    final var statistics = statisticsMessageMapper.map(messageMetadata);
    return switch (statistics.type()) {
      case GAME -> gameStatisticsService.retrieveStatistics(statistics);
//      todo: until person global service is not implemented
      case PLAYER_IN_GAME, PERSON_GLOBAL -> playerInGameStatisticsService.retrieveStatistics(statistics);
    };
  }

}
