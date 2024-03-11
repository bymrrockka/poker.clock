package by.mrrockka.service;

import by.mrrockka.domain.Withdrawals;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.features.calculation.CalculationService;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.EntriesAndWithdrawalAmountsAreNotEqualException;
import by.mrrockka.service.exception.GameSummaryNotFoundException;
import by.mrrockka.service.exception.PayoutsAreNotCalculatedException;
import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCalculationService {

  private final CalculationService calculationService;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage calculatePayments(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id.".
                formatted(messageMetadata.command(), messageMetadata.chatId()));

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    if (telegramGame.game() instanceof TournamentGame) {
      validateTournament((TournamentGame) telegramGame.game());
    }

    if (telegramGame.game() instanceof CashGame) {
      validateCash((CashGame) telegramGame.game());
    }

    final var payoutResponse = calculationService.calculate(telegramGame.game())
      .stream()
      .map(payout -> prettyPrintPayout(payout, telegramGame.game()))
      .reduce("%s\n%s"::formatted)
      .orElseThrow(PayoutsAreNotCalculatedException::new);

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(payoutResponse)
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  //   todo: add validation service
  private void validateTournament(final TournamentGame game) {
    if (isNull(game.getTournamentSummary())) {
      throw new GameSummaryNotFoundException();
    }
  }

  private void validateCash(final CashGame game) {
    final var totalEntries = game.getEntries().stream()
      .map(Entries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var totalWithdrawals = game.getWithdrawals().stream()
      .map(Withdrawals::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (totalEntries.compareTo(totalWithdrawals) < 0) {
      throw new EntriesAndWithdrawalAmountsAreNotEqualException();
    }
  }

  private String prettyPrintPayout(final Payout payout, final Game game) {
    final var strBuilder = new StringBuilder("-----------------------------\n");
    strBuilder.append("Payout to: @%s\n".formatted(payout.person().getNickname()));
    strBuilder.append("\tEntries: %s\n".formatted(payout.entries().total()));

    if (game instanceof TournamentGame) {
      strBuilder.append("\tPrize: %s\n".formatted(payout.entries().total().add(payout.totalDebts())));
    }

    if (game instanceof CashGame) {
      strBuilder.append("\tWithdrawals: %s\n".formatted(payout.withdrawals().total()));
    }

    strBuilder.append("\tTotal: %s\n".formatted(payout.totalDebts()));

    final var strDebtsOpt = payout.debts().stream()
      .map(debt -> "\t@%s -> %s".formatted(debt.person().getNickname(), debt.amount()))
      .reduce("%s\n%s"::formatted);

    if (strDebtsOpt.isPresent()) {
      strBuilder.append("From\n");
      strBuilder.append(strDebtsOpt.get());
      strBuilder.append('\n');
    }

    return strBuilder.toString();
  }

}
