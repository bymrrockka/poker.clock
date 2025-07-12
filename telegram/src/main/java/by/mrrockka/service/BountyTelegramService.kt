package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.parser.BountyMessageParser;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.EntriesForPersonNotFoundException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.bounty.BountyValidator;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BountyTelegramService {

  private final BountyService bountyService;
  private final BountyMessageParser bountyMessageParser;
  private final GameTelegramFacadeService gameTelegramFacadeService;
  private final PersonMentionsValidator personMentionsValidator;
  private final BountyValidator bountyValidator;

  public BotApiMethodMessage storeBounty(final MessageMetadata messageMetadata) {
    personMentionsValidator.validateMessageMentions(messageMetadata, 2);
    final var fromAndTo = bountyMessageParser.parse(messageMetadata);
    final var telegramGame = gameTelegramFacadeService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    final var game = telegramGame.game().asType(BountyGame.class);

    bountyValidator.validate(game, fromAndTo);

    final var gamePersons = game.getEntries().stream()
      .map(PersonEntries::person)
      .toList();

    final var bounty = Bounty.builder()
      .to(findByNickname(fromAndTo.getValue(), gamePersons))
      .from(findByNickname(fromAndTo.getKey(), gamePersons))
      .amount(game.asType(BountyGame.class).getBountyAmount())
      .build();

    bountyService.storeBounty(game.getId(), bounty, messageMetadata.getCreatedAt());

    return SendMessage.builder()
      .chatId(messageMetadata.getChatId())
      .text("Bounty amount %s from %s stored for %s"
              .formatted(game.getBountyAmount(), fromAndTo.getKey().getNickname(), fromAndTo.getValue().getNickname()))
      .replyToMessageId(telegramGame.messageMetadata().getId())
      .build();
  }

  private Person findByNickname(final TelegramPerson telegramPerson, final List<Person> persons) {
    return persons.stream()
      .filter(person -> person.getNickname().equals(telegramPerson.getNickname()))
      .findFirst()
      .orElseThrow(EntriesForPersonNotFoundException::new);
  }
}
