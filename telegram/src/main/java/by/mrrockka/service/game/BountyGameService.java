package by.mrrockka.service.game;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.mapper.game.GameMessageMapper;
import by.mrrockka.mapper.game.TelegramGameMapper;
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

@Slf4j
@Service
@RequiredArgsConstructor
class BountyGameService {

  private final TelegramGameRepository telegramGameRepository;
  private final TelegramPersonService telegramPersonService;
  private final GameService gameService;
  private final EntriesService entriesService;
  private final GameMessageMapper gameMessageMapper;
  private final TelegramGameMapper telegramGameMapper;

  @Transactional(isolation = Isolation.READ_COMMITTED)
  BotApiMethodMessage storeGame(final MessageMetadata messageMetadata) {
    final var game = gameMessageMapper.mapBounty(messageMetadata.text());
    final var personIds = telegramPersonService.storePersons(messageMetadata).stream()
      .map(Person::getId)
      .toList();
    gameService.storeBountyGame(game);
    telegramGameRepository.save(telegramGameMapper.toEntity(game, messageMetadata));
    entriesService.storeBatch(game.getId(), personIds, game.getBuyIn(), messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("Bounty tournament started.")
      .replyToMessageId(messageMetadata.id())
      .build();
  }
}
