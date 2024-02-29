package by.mrrockka.service;

import by.mrrockka.domain.game.Game;
import by.mrrockka.mapper.game.GameMessageMapper;
import by.mrrockka.repo.game.TelegramGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramGameService {

  private final TelegramGameRepository telegramGameRepository;
  private final TelegramPersonService telegramPersonService;
  private final GameService gameService;
  private final EntriesService entriesService;
  private final GameMessageMapper gameMessageMapper;

  //  todo: change return type to custom or List
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public BotApiMethodMessage storeGame(final Update update) {
    final var command = update.getMessage().getText();
    final var chatId = update.getMessage().getChatId();
    final var messageTimestamp = Instant.ofEpochSecond(update.getMessage().getDate());

    log.debug("Processing {\n%s\n} message from %s chat id. Timestamp %s".formatted(command, chatId, messageTimestamp));

    final var game = gameMessageMapper.map(command);
    final var personIds = telegramPersonService.storePersons(update);
    gameService.storeNewGame(game);
    telegramGameRepository.save(game.getId(), chatId, messageTimestamp);
    entriesService.storeBatch(game.getId(), personIds, game.getBuyIn(), messageTimestamp);

    return SendMessage.builder()
      .chatId(chatId)
      .text("Tournament started.")
      .build();
  }

  public Optional<Game> getGameByTimestampOrLatest(Long chatId, Instant createAt) {
    return Optional.ofNullable(createAt)
      .map(instant -> telegramGameRepository.findByChatIdAndCreatedAt(chatId, instant))
      .orElseGet(() -> telegramGameRepository.findLatestByChatId(chatId))
      .map(gameService::retrieveGame);
  }
}
