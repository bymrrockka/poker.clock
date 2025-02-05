package by.mrrockka.service.game;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.mapper.TelegramGameMapper;
import by.mrrockka.repo.game.TelegramGameRepository;
import by.mrrockka.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameTelegramFacadeService {

  private final TelegramGameRepository telegramGameRepository;
  private final TournamentGameService tournamentGameService;
  private final CashGameService cashGameService;
  private final BountyGameService bountyGameService;
  private final GameService gameService;
  private final TelegramGameMapper telegramGameMapper;

  public BotApiMethodMessage storeTournamentGame(final MessageMetadata messageMetadata) {
    return tournamentGameService.storeGame(messageMetadata);
  }

  public BotApiMethodMessage storeCashGame(final MessageMetadata messageMetadata) {
    return cashGameService.storeGame(messageMetadata);
  }

  public BotApiMethodMessage storeBountyGame(final MessageMetadata messageMetadata) {
    return bountyGameService.storeGame(messageMetadata);
  }

  public Optional<TelegramGame> getGameByMessageMetadata(final MessageMetadata messageMetadata) {
    return messageMetadata.optReplyTo()
      .map(replyTo -> telegramGameRepository.findByChatAndMessageId(messageMetadata.chatId(), replyTo.id()))
      .orElseGet(() -> telegramGameRepository.findLatestByChatId(messageMetadata.chatId()))
      .map(entity -> telegramGameMapper.toGame(gameService.retrieveGame(entity.gameId()), entity));
  }

}
