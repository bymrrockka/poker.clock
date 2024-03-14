package by.mrrockka.service;

import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.PrizePoolMessageMapper;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class TelegramPrizePoolService {

  private final PrizePoolService prizePoolService;
  private final PrizePoolMessageMapper prizePoolMessageMapper;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage storePrizePool(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var prizePool = prizePoolMessageMapper.map(messageMetadata.command());

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    if (!(telegramGame.game() instanceof TournamentGame)) {
      throw new ProcessingRestrictedException(GameType.TOURNAMENT);
    }

    prizePoolService.store(telegramGame.game().getId(), prizePool);
    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(prettyPrint(prizePool))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private String prettyPrint(final PrizePool prizePool) {
    return """
      Prize Pool stored:
      %s
      """.formatted(
      prizePool.positionAndPercentages().stream()
        .map(pp -> "\tposition: %s, percentage: %s".formatted(pp.position(), pp.percentage()))
        .reduce("%s\n%s"::formatted)
        .orElse(StringUtils.EMPTY)
    );
  }
}
