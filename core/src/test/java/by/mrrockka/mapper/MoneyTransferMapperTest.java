package by.mrrockka.mapper;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.MoneyTransferCreator;
import by.mrrockka.creator.PayerCreator;
import by.mrrockka.creator.PayoutCreator;
import by.mrrockka.domain.payout.TransferType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MoneyTransferMapperTest {

  private final MoneyTransferMapper moneyTransferMapper = Mappers.getMapper(MoneyTransferMapper.class);

  @Test
  void givenEntity_whenMap_thenShouldReturnDomain() {
    final var actual = MoneyTransferCreator.entity();
    final var expected = MoneyTransferCreator.domain();

    assertThat(moneyTransferMapper.map(actual)).isEqualTo(expected);
  }

  @Test
  void givenGameAndPayer_whenMap_thenShouldReturnEntity() {
    final var game = GameCreator.tournament();
    final var payer = PayerCreator.payer();
    final var expected = MoneyTransferCreator
      .entity(entity ->
                entity
                  .type(TransferType.DEBIT)
                  .gameId(game.getId())
                  .amount(payer.amount())
                  .personId(payer.person().getId())
      );

    assertThat(moneyTransferMapper.map(game.getId(), payer)).isEqualTo(expected);
  }

  @Test
  void givenGameAndPayout_whenMap_thenShouldReturnEntitiesList() {
    final var game = GameCreator.tournament();
    final var payout = PayoutCreator.payout();

    final var expected = payout.payers().stream()
      .map(payer -> MoneyTransferCreator
        .entity(entity ->
                  entity
                    .type(TransferType.DEBIT)
                    .gameId(game.getId())
                    .amount(payer.amount())
                    .personId(payer.person().getId())
        ))
      .collect(Collectors.toList());

    expected.add(
      MoneyTransferCreator
        .entity(entity ->
                  entity
                    .type(TransferType.CREDIT)
                    .gameId(game.getId())
                    .amount(payout.total())
                    .personId(payout.person().getId())
        ));

    assertThat(moneyTransferMapper.map(game.getId(), payout)).isEqualTo(expected);
  }

  @Test
  void givenGameAndPayoutsList_whenMap_thenShouldReturnEntitiesList() {
    final var game = GameCreator.tournament();
    final var payout = PayoutCreator.payout();

    final var expected = payout.payers().stream()
      .map(payer -> MoneyTransferCreator
        .entity(entity ->
                  entity
                    .type(TransferType.DEBIT)
                    .gameId(game.getId())
                    .amount(payer.amount())
                    .personId(payer.person().getId())
        ))
      .collect(Collectors.toList());

    expected.add(
      MoneyTransferCreator
        .entity(entity ->
                  entity
                    .type(TransferType.CREDIT)
                    .gameId(game.getId())
                    .amount(payout.total())
                    .personId(payout.person().getId())
        ));

    assertThat(moneyTransferMapper.map(game.getId(), List.of(payout))).isEqualTo(expected);
  }


}