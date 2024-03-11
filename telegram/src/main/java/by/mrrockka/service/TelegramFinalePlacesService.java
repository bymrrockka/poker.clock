package by.mrrockka.service;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.mapper.FinalePlacesMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.FinalPlaceContainsTelegramOfNotExistingPlayerException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramFinalePlacesService {

  private final FinalePlacesService finalePlacesService;
  private final FinalePlacesMessageMapper finalePlacesMessageMapper;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage storePrizePool(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var places = finalePlacesMessageMapper.map(messageMetadata.command());
    final var telegramPersons = telegramPersonService
      .getAllByTelegramsAndChatId(places.stream().map(Pair::getValue).toList(), messageMetadata.chatId());

    final var finalePlaces = new FinalePlaces(
      places.stream()
        .map(place -> FinalPlace.builder()
          .position(place.getKey())
          .person(telegramPersons.stream()
                    .filter(person -> person.getNickname().equals(place.getValue()))
                    .findAny()
                    .orElseThrow(() -> new FinalPlaceContainsTelegramOfNotExistingPlayerException(place.getValue())))
          .build())
        .toList());

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    finalePlacesService.store(telegramGame.game().getId(), finalePlaces);
    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(prettyPrint(finalePlaces, telegramPersons))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private String prettyPrint(final FinalePlaces finalePlaces, final List<TelegramPerson> telegramPersons) {
    return """
      Finale places:
      %s
      """.formatted(
      finalePlaces.finalPlaces().stream()
        .map(fp -> "\tposition: %s, telegram: @%s".formatted(fp.position(), telegramPersons.stream()
          .filter(person -> person.getId().equals(fp.person().getId()))
          .map(TelegramPerson::getNickname)
          .findAny().orElseThrow()))
        .reduce("%s\n%s"::formatted)
        .orElse(StringUtils.EMPTY)
    );
  }
}
