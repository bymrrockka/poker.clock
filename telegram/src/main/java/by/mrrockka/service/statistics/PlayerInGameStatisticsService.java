package by.mrrockka.service.statistics;

import by.mrrockka.domain.collection.PersonBounties;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.response.builder.PlayerInGameStatisticsResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramService;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class PlayerInGameStatisticsService {

  private final PlayerInGameStatisticsResponseBuilder playerInGameStatisticsResponseBuilder;
  private final GameTelegramService gameTelegramService;

  BotApiMethodMessage retrieveStatistics(final StatisticsCommand statisticsCommand) {
    final var messageMetadata = statisticsCommand.metadata();
    final var telegramGame = gameTelegramService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    final var game = telegramGame.game();

    final var nickname = statisticsCommand.metadata().optFromNickname()
      .orElseThrow(PlayerHasNoNicknameException::new);

    final var playerInGameDetails = PlayerInGameStatistics.builder()
      .personEntries(getPersonEntries(game, nickname))
      .personWithdrawals(getPersonWithdrawals(game, nickname).orElse(null))
      .personBounties(getPersonBounties(game, nickname).orElse(null))
      .moneyInGame(calculateMoneyInGame(game, nickname))
      .build();

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(playerInGameStatisticsResponseBuilder.response(playerInGameDetails))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

  private BigDecimal calculateMoneyInGame(final Game game, final String nickname) {
    final var personEntriesTotal = getPersonEntries(game, nickname).total();

    if (game.isType(CashGame.class)) {
      final var personWithdrawalsTotal = getPersonWithdrawals(game, nickname)
        .map(PersonWithdrawals::total)
        .orElse(BigDecimal.ZERO);
      return personEntriesTotal.subtract(personWithdrawalsTotal);
    }

    if (game.isType(BountyGame.class)) {
      final var personBountiesTotal = getPersonBounties(game, nickname)
        .map(PersonBounties::totalTaken)
        .orElse(BigDecimal.ZERO);
      return personEntriesTotal.multiply(BigDecimal.valueOf(2)).subtract(personBountiesTotal);
    }

    return personEntriesTotal;
  }

  private Optional<PersonBounties> getPersonBounties(final Game game, final String nickname) {
    if (game.isType(BountyGame.class)) {
      final var person = getPersonEntries(game, nickname).person();

      final var bounties = game.asType(BountyGame.class).getBountyList().stream()
        .filter(bounty -> bounty.from().equals(person) || bounty.to().equals(person))
        .toList();

      return Optional.of(PersonBounties.builder()
                           .bounties(bounties)
                           .person(person)
                           .build());
    }

    return Optional.empty();
  }

  private Optional<PersonWithdrawals> getPersonWithdrawals(final Game game, final String nickname) {
    if (game.isType(CashGame.class)) {
      return game.asType(CashGame.class).getWithdrawals().stream()
        .filter(personWithdrawals -> personWithdrawals.person().getNickname().equals(nickname))
        .findFirst();
    }

    return Optional.empty();
  }

  private PersonEntries getPersonEntries(final Game game, final String nickname) {
    return game.getEntries().stream()
      .filter(personEntries -> personEntries.person().getNickname().equals(nickname))
      .findFirst()
      .orElseThrow();
  }

}
