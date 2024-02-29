package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.features.accounting.Accounting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCalculationService {

  private final Accounting accounting;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;

  public BotApiMethodMessage calculatePayments(Update update) {
    final var command = update.getMessage().getText();
    final var chatId = update.getMessage().getChatId();
    final var pinnedMessageTimestamp = Optional.ofNullable(update.getMessage().getPinnedMessage())
      .map(Message::getDate)
      .map(Instant::ofEpochSecond)
      .orElse(null);

    log.debug("Processing {\n%s\n} message from %s chat id.".formatted(command, chatId));

    final var game = telegramGameService.getGameByTimestampOrLatest(chatId, pinnedMessageTimestamp)
      .orElseThrow(); //todo: add meaningful exception

    validateGame(game);
    final var telegramPersons = telegramPersonService.getAllByGameId(game.getId());
    final var payoutResponse = accounting.calculate(game)
      .stream()
      .map(payout -> prettyPrintPayout(payout, telegramPersons))
      .reduce("%s\n%s"::formatted)
      .orElseThrow();

    return SendMessage.builder()
      .chatId(chatId)
      .text(payoutResponse)
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
