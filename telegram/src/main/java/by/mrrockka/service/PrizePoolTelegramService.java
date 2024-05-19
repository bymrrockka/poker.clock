package by.mrrockka.service;

import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.PrizePoolMessageMapper;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.response.builder.PrizePoolResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import by.mrrockka.service.game.GameTelegramService;
import by.mrrockka.validation.GameValidator;
import by.mrrockka.validation.prizepool.PrizePoolValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class PrizePoolTelegramService {

  private final PrizePoolService prizePoolService;
  private final PrizePoolMessageMapper prizePoolMessageMapper;
  private final GameTelegramService gameTelegramService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final GameValidator gameValidator;
  private final PrizePoolValidator prizePoolValidator;
  private final PrizePoolResponseBuilder prizePoolResponseBuilder;

  public BotApiMethodMessage storePrizePool(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var prizePool = prizePoolMessageMapper.map(messageMetadata.command());
    prizePoolValidator.validate(prizePool);

    final var telegramGame = gameTelegramService
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
