package by.mrrockka.validation.bounty;

import by.mrrockka.domain.Bounty;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.repo.game.GameType;
import by.mrrockka.service.exception.PersonsCantBeEqualForBountyException;
import by.mrrockka.service.exception.PlayerHasNotEnoughEntriesException;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiPredicate;

@Component
public class BountyValidator {

  public void validate(final Game game, final Pair<TelegramPerson, TelegramPerson> fromAndTo) {
    if (!game.isType(BountyGame.class)) {
      throw new ProcessingRestrictedException(GameType.BOUNTY);
    }

    final var bountyGame = game.asType(BountyGame.class);

    if (fromAndTo.getValue().equals(fromAndTo.getKey())) {
      throw new PersonsCantBeEqualForBountyException(fromAndTo.getKey().getNickname());
    }
//  validate from person has enough entries
    validateBounties(bountyGame.getEntries(), bountyGame.getBountyList(), fromAndTo.getKey().getNickname(),
                     (bountySize, entriesSize) -> bountySize >= entriesSize);
//  validate to person has enough entries
    validateBounties(bountyGame.getEntries(), bountyGame.getBountyList(), fromAndTo.getValue().getNickname(),
                     (bountySize, entriesSize) -> bountySize.intValue() == entriesSize);
  }

  private void validateBounties(final List<PersonEntries> entries, final List<Bounty> bounties,
                                final String nickname, final BiPredicate<Integer, Integer> check) {
    final var personEntries = entries.stream()
      .filter(entry -> entry.person().getNickname().equals(nickname))
      .findFirst()
      .map(PersonEntries::entries);
    final var personFromBounties = bounties.stream()
      .filter(bounty -> bounty.from().getNickname().equals(nickname))
      .toList();

    if (personEntries.isEmpty() || check.test(personFromBounties.size(), personEntries.get().size())) {
      throw new PlayerHasNotEnoughEntriesException(nickname);
    }
  }

}
