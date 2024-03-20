package by.mrrockka.service;

import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.features.calculation.CalculationService;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.response.builder.CalculationOutputBuilder;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCalculationService {

  private final CalculationService calculationService;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final CalculationOutputBuilder calculationOutputBuilder;

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

    final var payouts = calculationService.calculate(telegramGame.game());
    if (payouts.isEmpty()) {
      throw new PayoutsAreNotCalculatedException();
    }

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(calculationOutputBuilder.buildPayout(payouts, telegramGame.game()))
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

}
