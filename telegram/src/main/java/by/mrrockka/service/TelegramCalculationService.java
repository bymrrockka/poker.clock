package by.mrrockka.service;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.features.calculation.CalculationService;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.service.exception.*;
import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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

    if (telegramGame.game().isTournament()) {
      validateTournament(telegramGame.game().asTournament());
    }

    if (telegramGame.game().isCash()) {
      validateCash(telegramGame.game().asCash());
    }

    if (telegramGame.game().isBounty()) {
      validateBounty(telegramGame.game().asBounty());
    }

    final var finaleResponse = prettyPrintFinaleSummary(telegramGame.game());
    final var payoutResponse = calculationService.calculate(telegramGame.game())
      .stream()
      .map(payout -> prettyPrintPayout(payout, telegramGame.game()))
      .reduce("%s\n%s"::formatted)
      .orElseThrow(PayoutsAreNotCalculatedException::new);

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(finaleResponse + payoutResponse)
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  //   todo: add validation service
  private void validateTournament(final TournamentGame game) {
    if (isNull(game.getFinaleSummary())) {
      throw new FinaleSummaryNotFoundException();
    }
  }

  private void validateBounty(final BountyGame game) {
    if (isNull(game.getFinaleSummary())) {
      throw new FinaleSummaryNotFoundException();
    }

    final var bountiesCount = game.getBountyList().size() + 1;
    final var entriesCount = game.getEntries().stream()
      .mapToInt(entry -> entry.entries().size())
      .sum();

    if (entriesCount != bountiesCount) {
      throw new BountiesAndEntriesSizeAreNotEqualException(entriesCount - bountiesCount);
    }
  }

  private void validateCash(final CashGame game) {
    final var totalEntries = game.getEntries().stream()
      .map(PersonEntries::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    final var totalWithdrawals = game.getWithdrawals().stream()
      .map(PersonWithdrawals::total)
      .reduce(BigDecimal::add)
      .orElse(BigDecimal.ZERO);

    if (totalEntries.compareTo(totalWithdrawals) != 0) {
      throw new EntriesAndWithdrawalAmountsAreNotEqualException(totalEntries.subtract(totalWithdrawals));
    }
  }

  //  todo: move to all pretty prints to specific service
  private String prettyPrintPayout(final Payout payout, final Game game) {
    final var strBuilder = new StringBuilder("-----------------------------\n");
    strBuilder.append("Payout to: @");
    strBuilder.append(payout.person().getNickname());
    strBuilder.append("\n");

    strBuilder.append("\tEntries: ");
    if (game.isCash()) {
      strBuilder.append(payout.personEntries().total());
    } else {
      strBuilder.append(payout.personEntries().entries().size());
    }
    strBuilder.append("\n");

    if (game.isTournament()) {
      strBuilder.append("\tPrize: ");
      strBuilder.append(payout.total());
      strBuilder.append(" (won ");
      strBuilder.append(game.asTournament().getFinaleSummary().getPrizeFor(payout.person()));
      strBuilder.append(" - entries ");
      strBuilder.append(payout.personEntries().total());
      strBuilder.append(")\n");
    }

    if (game.isBounty()) {
      final var totalBounties = payout.personBounties().totalTaken().subtract(payout.personBounties().totalGiven());
      strBuilder.append("\tBounties: ");
      strBuilder.append(totalBounties);
      strBuilder.append(" (");

      if (!payout.personBounties().taken().isEmpty()) {
        strBuilder.append("taken ");
        strBuilder.append(payout.personBounties().taken().size());
      }

      if (!payout.personBounties().taken().isEmpty() && !payout.personBounties().given().isEmpty()) {
        strBuilder.append(" - ");
      }

      if (!payout.personBounties().given().isEmpty()) {
        strBuilder.append("given ");
        strBuilder.append(payout.personBounties().given().size());
      }

      strBuilder.append(")\n");

      strBuilder.append("\tPrize: ");
      strBuilder.append(payout.total().subtract(totalBounties));
      strBuilder.append(" (won ");
      strBuilder.append(game.asTournament().getFinaleSummary().getPrizeFor(payout.person()));
      strBuilder.append(" - entries ");
      strBuilder.append(payout.personEntries().total());
      strBuilder.append(")\n");
    }

    if (game.isCash()) {
      strBuilder.append("\tWithdrawals: ");
      strBuilder.append(payout.personWithdrawals().total());
      strBuilder.append("\n");
    }

    strBuilder.append("\tTotal: ");
    strBuilder.append(payout.total());
    strBuilder.append("\n");

    final var strDebtsOpt = payout.payers().stream()
      .map(debt -> "\t@%s -> %s".formatted(debt.person().getNickname(), debt.amount()))
      .reduce("%s\n%s"::formatted);

    if (strDebtsOpt.isPresent()) {
      strBuilder.append("From\n");
      strBuilder.append(strDebtsOpt.get());
      strBuilder.append("\n");
    }

    return strBuilder.toString();
  }

  private String prettyPrintFinaleSummary(final Game game) {
    if (game.isTournament() || game.isBounty()) {
      final var placesStr = game.asTournament().getFinaleSummary().finaleSummaries().stream()
        .map(place -> "%s. @%s won %s".formatted(place.position(), place.person().getNickname(), place.amount()))
        .reduce("%s\n%s"::formatted)
        .orElse(EMPTY);
      final var total = game.asTournament().getFinaleSummary().total();

      return """
        -----------------------------
        %s
        Total: %s
                
        """.formatted(placesStr, total);
    }
    return EMPTY;
  }

}
