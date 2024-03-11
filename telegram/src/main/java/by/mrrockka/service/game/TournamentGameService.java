package by.mrrockka.service.game;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.game.TelegramGameMapper;
import by.mrrockka.mapper.game.TournamentMessageMapper;
import by.mrrockka.repo.game.TelegramGameRepository;
import by.mrrockka.service.EntriesService;
import by.mrrockka.service.GameService;
import by.mrrockka.service.TelegramPersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
class TournamentGameService {

  private final TelegramGameRepository telegramGameRepository;
  private final TelegramPersonService telegramPersonService;
  private final GameService gameService;
  private final EntriesService entriesService;
  private final TournamentMessageMapper tournamentMessageMapper;
  private final MessageMetadataMapper messageMetadataMapper;
  private final TelegramGameMapper telegramGameMapper;

  @Transactional(isolation = Isolation.READ_COMMITTED)
  BotApiMethodMessage storeGame(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id. Timestamp %s"
                .formatted(messageMetadata.command(), messageMetadata.chatId(), messageMetadata.createdAt()));

    final var game = tournamentMessageMapper.mapTournament(messageMetadata.command());
    final var personIds = telegramPersonService.storePersons(update).stream()
      .map(Person::getId)
      .toList();
    gameService.storeTournamentGame(game);
    telegramGameRepository.save(telegramGameMapper.toEntity(game, messageMetadata));
    entriesService.storeBatch(game.getId(), personIds, game.getBuyIn(), messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("Tournament started.")
      .replyToMessageId(messageMetadata.id())
      .build();
  }
}
