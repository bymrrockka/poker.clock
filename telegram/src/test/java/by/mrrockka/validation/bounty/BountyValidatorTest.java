package by.mrrockka.validation.bounty;

import by.mrrockka.creator.*;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.domain.game.BountyGame;
import by.mrrockka.domain.game.Game;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BountyValidatorTest {

  private static final BigDecimal BUYIN = BigDecimal.ONE;
  private static final BigDecimal STACK = BigDecimal.ONE;
  private static final UUID GAME_ID = UUID.randomUUID();
  private static final String FROM_NICKNAME = "from_nickname";
  private static final String TO_NICKNAME = "to_nickname";
  private static final TelegramPerson FROM = TelegramPersonCreator.domain(FROM_NICKNAME);
  private static final TelegramPerson TO = TelegramPersonCreator.domain(TO_NICKNAME);

  private final BountyValidator bountyValidator = new BountyValidator();

  @Test
  void givenValidBountyGame_whenValidateExecuted_thenShouldNotThrowExceptions() {
    final var game =
      GameCreator.bounty(builder -> builder
        .entries(List.of(
          EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(FROM_NICKNAME))),
          EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(TO_NICKNAME)))
        ))
        .bountyList(Collections.emptyList()));

    final var fromAndTo = Pair.of(FROM, TO);

    assertThatCode(() -> bountyValidator.validate(game, fromAndTo)).doesNotThrowAnyException();
  }

  @Test
  void givenValidBountyGameAndFromAndToIsSamePerson_whenValidateExecuted_thenShouldThrowExceptions() {
    final var game = GameCreator.bounty();
    final var fromAndTo = Pair.of(FROM, FROM);
    assertThatThrownBy(() -> bountyValidator.validate(game, fromAndTo))
      .isInstanceOf(PersonsCantBeEqualForBountyException.class);
  }

  private static Stream<Arguments> invalidBountiesSize() {
    return Stream.of(
//    case#1 From person already gave his bounty
      Arguments.of(
        GameCreator.bounty(builder -> builder
          .entries(List.of(
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(FROM_NICKNAME))),
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(TO_NICKNAME)))
          ))
          .bountyList(List.of(BountyCreator.bounty(bounty -> bounty.from(FROM)))))
      ),
//    case#2 To person already gave his bounty
      Arguments.of(
        GameCreator.bounty(builder -> builder
          .entries(List.of(
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(FROM_NICKNAME))),
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(TO_NICKNAME)))
          ))
          .bountyList(List.of(BountyCreator.bounty(bounty -> bounty.from(TO)))))
      ),
//    case#3 From person has more entries and already gave all his bounties
      Arguments.of(
        GameCreator.bounty(builder -> builder
          .entries(List.of(
            EntriesCreator.entries(entries -> entries
              .person(PersonCreator.domain(FROM_NICKNAME))
              .entries(List.of(BUYIN, BUYIN))),
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(TO_NICKNAME)))
          ))
          .bountyList(List.of(
            BountyCreator.bounty(bounty -> bounty.from(FROM)),
            BountyCreator.bounty(bounty -> bounty.from(FROM))
          )))
      ),
//    case#4 From person has more entries and already gave all his bounties
      Arguments.of(
        GameCreator.bounty(builder -> builder
          .entries(List.of(
            EntriesCreator.entries(entries -> entries.person(PersonCreator.domain(FROM_NICKNAME))),
            EntriesCreator.entries(entries -> entries
              .person(PersonCreator.domain(TO_NICKNAME))
              .entries(List.of(BUYIN, BUYIN)))
          ))
          .bountyList(List.of(
            BountyCreator.bounty(bounty -> bounty.from(TO)),
            BountyCreator.bounty(bounty -> bounty.from(TO))
          )))
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidBountiesSize")
  void givenInvalidBountyGame_whenValidateExecuted_thenShouldThrowExceptions(final BountyGame game) {
    final var fromAndTo = Pair.of(FROM, TO);

    assertThatThrownBy(() -> bountyValidator.validate(game, fromAndTo))
      .isInstanceOf(PlayerHasNotEnoughEntriesException.class);
  }

  private static Stream<Arguments> invalidGameType() {
    return Stream.of(
      Arguments.of(GameCreator.cash()),
      Arguments.of(GameCreator.tournament())
    );
  }

  @ParameterizedTest
  @MethodSource("invalidGameType")
  void givenDifferentGameType_whenValidateExecuted_thenShouldNotThrowExceptions(final Game game) {
    final var fromAndTo = Pair.of(FROM, TO);
    assertThatThrownBy(() -> bountyValidator.validate(game, fromAndTo))
      .isInstanceOf(ProcessingRestrictedException.class);
  }


}
