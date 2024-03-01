package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.features.accounting.Accounting;
import by.mrrockka.mapper.MessageMetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCalculationService {

  private final Accounting accounting;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage calculatePayments(Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id.".
                formatted(messageMetadata.command(), messageMetadata.chatId()));

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(); //todo: add meaningful exception

    validateGame(telegramGame.game());
    final var telegramPersons = telegramPersonService.getAllByGameId(telegramGame.game().getId());
    final var payoutResponse = accounting.calculate(telegramGame.game())
      .stream()
      .map(payout -> prettyPrintPayout(payout, telegramPersons))
      .reduce("%s\n%s"::formatted)
      .orElseThrow();

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(payoutResponse)
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private void validateGame(Game game) {
    if (isNull(game.getGameSummary())) {
      throw new RuntimeException("No finale places or prize pool specified, can't calculate"); //todo:
    }
  }

  private String prettyPrintPayout(Payout payout, List<TelegramPerson> telegramPersons) {
    final var strBuilder = new StringBuilder("-----------------------------\n");
    strBuilder.append("Payout to: %s\n".formatted(getPlayerTelegram(payout.creditor().person(), telegramPersons)));
    strBuilder.append("From\n");
    final var strDebts = payout.debts().stream()
      .map(debt -> Pair.of(getPlayerTelegram(debt.debtor().person(), telegramPersons), debt.amount().toString()))
      .map(pair -> "\t%s -> %s".formatted(pair.getKey(), pair.getValue()))
      .reduce("%s\n%s"::formatted)
      .orElseThrow();

    strBuilder.append(strDebts);
    strBuilder.append('\n');

    return strBuilder.toString();
  }

  private String getPlayerTelegram(Person person, List<TelegramPerson> telegramPersons) {
    return '@' + telegramPersons.stream()
      .filter(telegramPerson -> telegramPerson.getId().equals(person.getId()))
      .map(TelegramPerson::getTelegram)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Person %s does not have telegram".formatted(person.getId())));
  }
}
