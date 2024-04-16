package by.mrrockka.service;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.mapper.BountyMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.*;
import by.mrrockka.service.game.TelegramGameService;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBountyService {

  private final BountyService bountyService;
  private final BountyMessageMapper bountyMessageMapper;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final PersonMentionsValidator personMentionsValidator;

  public BotApiMethodMessage storeBounty(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    personMentionsValidator.validateMessageMentions(messageMetadata);
    final var fromAndTo = bountyMessageMapper.map(messageMetadata);
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    final var game = telegramGame.game();

    if (game.isType(BountyGame.class)) {
      validate(game.asType(BountyGame.class), fromAndTo);
    } else {
      throw new ProcessingRestrictedException(GameType.BOUNTY);
    }

    final var gamePersons = game.getEntries().stream()
      .map(PersonEntries::person)
      .toList();

    final var bounty = Bounty.builder()
      .to(findByTelegram(fromAndTo.getValue(), gamePersons))
      .from(findByTelegram(fromAndTo.getKey(), gamePersons))
      .amount(game.asBounty().getBountyAmount())
      .build();

    bountyService.storeBounty(game.getId(), bounty, messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("Bounty amount %s from %s stored for %s"
              .formatted(game.asType(BountyGame.class).getBountyAmount(),
                         fromAndTo.getKey().getNickname(), fromAndTo.getValue().getNickname()))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private void validate(final BountyGame game, final Pair<TelegramPerson, TelegramPerson> fromAndTo) {
    if (fromAndTo.getValue().equals(fromAndTo.getKey())) {
      throw new PersonsCantBeEqualForBountyException(fromAndTo.getKey().getNickname());
    }

    final var fromEntries = game.getEntries().stream()
      .filter(entry -> entry.person().getNickname().equals(fromAndTo.getKey().getNickname()))
      .findFirst()
      .map(PersonEntries::entries);
    final var fromBounties = game.getBountyList().stream()
      .filter(bounty -> bounty.from().getNickname().equals(fromAndTo.getKey().getNickname()))
      .toList();

    if (fromEntries.isEmpty() || fromBounties.size() >= fromEntries.get().size()) {
      throw new PlayerHasNotEnoughEntriesException(fromAndTo.getKey().getNickname());
    }

    final var toEntries = game.getEntries().stream()
      .filter(entry -> entry.person().getNickname().equals(fromAndTo.getValue().getNickname()))
      .findFirst()
      .map(PersonEntries::entries);
    final var toBounties = game.getBountyList().stream()
      .filter(bounty -> bounty.from().getNickname().equals(fromAndTo.getValue().getNickname()))
      .toList();

    if (toEntries.isEmpty() || toBounties.size() == toEntries.get().size()) {
      throw new PlayerHasNotEnoughEntriesException(fromAndTo.getValue().getNickname());
    }
  }

  private Person findByTelegram(final TelegramPerson telegram, final List<Person> persons) {
    return persons.stream()
      .filter(person -> person.getNickname().equals(telegram.getNickname()))
      .findFirst()
      .orElseThrow(EntriesForPersonNotFoundException::new);
  }

}
