package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.finaleplaces.FinalPlace;
import by.mrrockka.domain.finaleplaces.FinalePlaces;
import by.mrrockka.mapper.finaleplaces.FinalePlacesMessageMapper;
import by.mrrockka.response.builder.FinalePlacesResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.FinalPlaceContainsNicknameOfNonExistingPlayerException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.GameValidator;
import by.mrrockka.validation.collection.CollectionsValidator;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinalePlacesTelegramService {

  private final FinalePlacesService finalePlacesService;
  private final FinalePlacesMessageMapper finalePlacesMessageMapper;
  private final GameTelegramFacadeService gameTelegramFacadeService;
  private final TelegramPersonService telegramPersonService;
  private final FinalePlacesResponseBuilder finalePlacesResponseBuilder;
  private final GameValidator gameValidator;
  private final PersonMentionsValidator personMentionsValidator;
  private final CollectionsValidator collectionsValidator;

  public BotApiMethodMessage storePrizePool(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageMentions(messageMetadata, 1);

    final var places = finalePlacesMessageMapper.map(messageMetadata);
    collectionsValidator.validateMapIsNotEmpty(places, "Finale places");

    final var telegramGame = gameTelegramFacadeService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    gameValidator.validateGameIsTournamentType(telegramGame.game());

    final var telegramPersons = telegramPersonService
      .getAllByNicknamesAndChatId(places.values().stream().map(TelegramPerson::getNickname).toList(),
                                  messageMetadata.chatId());

    final var finalePlaces = new FinalePlaces(
      places.entrySet().stream()
        .map(place -> FinalPlace.builder()
          .position(place.getKey())
          .person(findByNickname(telegramPersons, place.getValue().getNickname()))
          .build())
        .toList());

    finalePlacesService.store(telegramGame.game().getId(), finalePlaces);
    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(finalePlacesResponseBuilder.response(finalePlaces))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private TelegramPerson findByNickname(final List<TelegramPerson> telegramPersons, final String nickname) {
    return telegramPersons.stream()
      .filter(person -> person.getNickname().equals(nickname))
      .findAny()
      .orElseThrow(() -> new FinalPlaceContainsNicknameOfNonExistingPlayerException(nickname));
  }

}
