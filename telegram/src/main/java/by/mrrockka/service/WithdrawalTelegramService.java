package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.WithdrawalMessageMapper;
import by.mrrockka.response.builder.WithdrawalResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramService;
import by.mrrockka.validation.GameValidator;
import by.mrrockka.validation.collection.CollectionsValidator;
import by.mrrockka.validation.mentions.PersonMentionsValidator;
import by.mrrockka.validation.withdrawals.WithdrawalsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class WithdrawalTelegramService {

  private final WithdrawalsService withdrawalsService;
  private final WithdrawalMessageMapper withdrawalMessageMapper;
  private final GameTelegramService gameTelegramService;
  private final TelegramPersonService telegramPersonService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final GameValidator gameValidator;
  private final PersonMentionsValidator personMentionsValidator;
  private final WithdrawalResponseBuilder withdrawalResponseBuilder;
  private final CollectionsValidator collectionsValidator;
  private final WithdrawalsValidator withdrawalsValidator;

  public BotApiMethodMessage storeWithdrawal(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    personMentionsValidator.validateMessageMentions(messageMetadata, 1);

    final var personAndAmountMap = withdrawalMessageMapper.map(messageMetadata);
    collectionsValidator.validateMapIsNotEmpty(personAndAmountMap, "Withdrawal");

    final var amount = personAndAmountMap.values().stream().findFirst().orElseThrow();
    final var telegramGame = gameTelegramService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);
    gameValidator.validateGameIsCashType(telegramGame.game());
    withdrawalsValidator.validateWithdrawalsAgainstEntries(personAndAmountMap,
                                                           telegramGame.game().asType(CashGame.class));

    final var persons = telegramPersonService.getAllByNicknamesAndChatId(
      personAndAmountMap.keySet().stream().map(TelegramPerson::getNickname).toList(), messageMetadata.chatId());

    withdrawalsService.storeBatch(telegramGame.game().getId(), persons.stream().map(Person::getId).toList(), amount,
                                  messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(withdrawalResponseBuilder.response(persons, amount))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
