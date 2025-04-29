package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.parser.PrizePoolMessageParser;
import by.mrrockka.domain.GameType;
import by.mrrockka.response.builder.PrizePoolResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.GameValidator;
import by.mrrockka.validation.prizepool.PrizePoolValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class PrizePoolTelegramService {

  private final PrizePoolService prizePoolService;
  private final PrizePoolMessageParser prizePoolMessageParser;
  private final GameTelegramFacadeService gameTelegramFacadeService;
  private final GameValidator gameValidator;
  private final PrizePoolValidator prizePoolValidator;
  private final PrizePoolResponseBuilder prizePoolResponseBuilder;

  public BotApiMethodMessage storePrizePool(final MessageMetadata messageMetadata) {
    final var prizePool = prizePoolMessageParser.parse(messageMetadata.text());
    prizePoolValidator.validate(prizePool);

    final var telegramGame = gameTelegramFacadeService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    gameValidator.validateGameIsTournamentType(telegramGame.game());

    if (!(telegramGame.game() instanceof TournamentGame)) {
      throw new ProcessingRestrictedException(GameType.TOURNAMENT);
    }

    prizePoolService.store(telegramGame.game().getId(), prizePool);
    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(prizePoolResponseBuilder.response(prizePool))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

}
