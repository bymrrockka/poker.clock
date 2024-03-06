package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.game.GameMessageMapper;
import by.mrrockka.mapper.game.TelegramGameMapper;
import by.mrrockka.repo.game.TelegramGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
  private final MessageMetadataMapper messageMetadataMapper;
  private final TelegramGameMapper telegramGameMapper;

  //  todo: change return type to custom or List to support multiple commands in a message
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public BotApiMethodMessage storeGame(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id. Timestamp %s"
                .formatted(messageMetadata.command(), messageMetadata.chatId(), messageMetadata.createdAt()));

    final var game = gameMessageMapper.map(messageMetadata.command());
    final var personIds = telegramPersonService.storePersons(update).stream()
      .map(Person::getId)
      .toList();
    gameService.storeNewGame(game);
    telegramGameRepository.save(telegramGameMapper.toEntity(game, messageMetadata));
    entriesService.storeBatch(game.getId(), personIds, game.getBuyIn(), messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("Tournament started.")
      .replyToMessageId(messageMetadata.id())
      .build();
  }

  public Optional<TelegramGame> getGameByMessageMetadata(MessageMetadata messageMetadata) {
    return messageMetadata.optReplyTo()
      .map(replyTo -> telegramGameRepository.findByChatAndMessageId(messageMetadata.chatId(), replyTo.id()))
      .orElseGet(() -> telegramGameRepository.findLatestByChatId(messageMetadata.chatId()))
      .map(entity -> telegramGameMapper.toDomain(gameService.retrieveGame(entity.gameId()), entity));
  }
}
