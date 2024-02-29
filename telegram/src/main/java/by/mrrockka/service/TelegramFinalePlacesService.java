package by.mrrockka.service;

import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.domain.game.Game;
import by.mrrockka.mapper.FinalePlacesMessageMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramFinalePlacesService {

  private final FinalePlacesService finalePlacesService;
  private final FinalePlacesMessageMapper finalePlacesMessageMapper;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final UsernameReplaceUtil usernameReplaceUtil;

  public BotApiMethodMessage storePrizePool(Update update) {
    final var command = usernameReplaceUtil.replaceUsername(update);
    final var chatId = update.getMessage().getChatId();
    final var pinnedMessageTimestamp = Optional.ofNullable(update.getMessage().getPinnedMessage())
      .map(Message::getDate)
      .map(Instant::ofEpochSecond)
      .orElse(null);

    final var places = finalePlacesMessageMapper.map(command);
    final var persons = telegramPersonService
      .getAllByTelegramsAndChatId(places.stream().map(Pair::getValue).toList(), chatId);

    final var finalePlaces = new FinalePlaces(
      places.stream()
        .map(place -> FinalPlace.builder()
          .position(place.getKey())
          .person(persons.stream()
                    .filter(person -> person.getTelegram().equals(place.getValue()))
                    .findAny().orElseThrow())//todo: added meaningful exception
          .build())
        .toList());

    final var gameId = telegramGameService.getGameByTimestampOrLatest(chatId, pinnedMessageTimestamp)
      .map(Game::getId)
      .orElseThrow(); //todo: add meaningful exception

    finalePlacesService.store(gameId, finalePlaces);
    return SendMessage.builder()
      .chatId(chatId)
      .text("Finale places for game %s stored.".formatted(gameId))
      .build();
  }
}
