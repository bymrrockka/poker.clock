package by.mrrockka.service;

import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.features.accounting.Accounting;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.GameSummaryNotFoundException;
import by.mrrockka.service.exception.PayoutsAreNotCalculatedException;
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

  public BotApiMethodMessage calculatePayments(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id.".
                formatted(messageMetadata.command(), messageMetadata.chatId()));

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    validateGame(telegramGame.game());
    final var telegramPersons = telegramPersonService.getAllByGameId(telegramGame.game().getId());
    final var payoutResponse = accounting.calculate(telegramGame.game())
      .stream()
      .map(payout -> prettyPrintPayout(payout, telegramPersons))
      .reduce("%s\n%s"::formatted)
      .orElseThrow(PayoutsAreNotCalculatedException::new);

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(payoutResponse)
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  //   todo: add validation service
  private void validateGame(final TournamentGame game) {
    if (isNull(game.getTournamentGameSummary())) {
      throw new GameSummaryNotFoundException();
    }
  }

  //  todo: move to some service
  private String prettyPrintPayout(final Payout payout, final List<TelegramPerson> telegramPersons) {
    final var strBuilder = new StringBuilder("-----------------------------\n");
    strBuilder.append("Payout to: @%s\n".formatted(payout.creditorEntries().person().getNickname()));
    strBuilder.append("\tEntries: %s\n".formatted(payout.creditorEntries().total()));
    strBuilder.append("\tPrize: %s\n".formatted(payout.creditorEntries().total().add(payout.totalDebts())));
    strBuilder.append("\tTotal: %s\n".formatted(payout.totalDebts()));

    final var strDebtsOpt = payout.debts().stream()
      .map(debt -> Pair.of(debt.debtorEntries().person().getNickname(), debt.amount().toString()))
      .map(pair -> "\t@%s -> %s".formatted(pair.getKey(), pair.getValue()))
      .reduce("%s\n%s"::formatted);

    if (strDebtsOpt.isPresent()) {
      strBuilder.append("From\n");
      strBuilder.append(strDebtsOpt.get());
      strBuilder.append('\n');
    }

    return strBuilder.toString();
  }

}
