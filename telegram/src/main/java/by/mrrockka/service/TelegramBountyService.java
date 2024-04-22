package by.mrrockka.service;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.mapper.BountyMessageMapper;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.EntriesForPersonNotFoundException;
import by.mrrockka.service.game.TelegramGameService;
import by.mrrockka.validation.bounty.BountyValidator;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import lombok.RequiredArgsConstructor;
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
  private final BountyValidator bountyValidator;

  public BotApiMethodMessage storeBounty(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    personMentionsValidator.validateMessageMentions(messageMetadata);
    final var fromAndTo = bountyMessageMapper.map(messageMetadata);
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    final var game = telegramGame.game().asType(BountyGame.class);

    bountyValidator.validate(game, fromAndTo);

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
              .formatted(game.getBountyAmount(), fromAndTo.getKey().getNickname(), fromAndTo.getValue().getNickname()))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private Person findByTelegram(final TelegramPerson telegram, final List<Person> persons) {
    return persons.stream()
      .filter(person -> person.getNickname().equals(telegram.getNickname()))
      .findFirst()
      .orElseThrow(EntriesForPersonNotFoundException::new);
  }
}
