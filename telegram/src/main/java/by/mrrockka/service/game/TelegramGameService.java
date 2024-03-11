package by.mrrockka.service.game;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.mapper.game.TelegramGameMapper;
import by.mrrockka.repo.game.TelegramGameRepository;
import by.mrrockka.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramGameService {

  private final TelegramGameRepository telegramGameRepository;
  private final TournamentGameService tournamentGameService;
  private final CashGameService cashGameService;
  private final GameService gameService;
  private final TelegramGameMapper telegramGameMapper;

  //  todo: change return type to custom or List to support multiple commands in a message

  public BotApiMethodMessage storeTournament(final Update update) {
    return tournamentGameService.storeGame(update);
  }

  public BotApiMethodMessage storeCash(final Update update) {
    return cashGameService.storeGame(update);
  }

  public Optional<TelegramGame<Game>> getGameByMessageMetadata(final MessageMetadata messageMetadata) {
    return messageMetadata.optReplyTo()
      .map(replyTo -> telegramGameRepository.findByChatAndMessageId(messageMetadata.chatId(), replyTo.id()))
      .orElseGet(() -> telegramGameRepository.findLatestByChatId(messageMetadata.chatId()))
      .map(entity -> telegramGameMapper.toGame(gameService.retrieveGame(entity.gameId()), entity));
  }

}
