package by.mrrockka.mapper;

import by.mrrockka.domain.MoneyTransfer;
import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.Payout;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.repo.moneytransfer.MoneyTransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(imports = TransferType.class)
public interface MoneyTransferMapper {

  default List<MoneyTransferEntity> map(final UUID gameId, final Payout payout) {
    final var moneyTransfers = payout.payers().stream()
      .map(payer -> map(gameId, payer))
      .collect(Collectors.toList());

    final var payoutMapping = MoneyTransferEntity.builder()
      .type(TransferType.CREDIT)
      .amount(payout.total())
      .personId(payout.person().getId())
      .gameId(gameId)
      .build();

    moneyTransfers.add(payoutMapping);

    return moneyTransfers;
  }

  default List<MoneyTransferEntity> map(final UUID gameId, final List<Payout> payouts) {
    return payouts.stream()
      .flatMap(payout -> map(gameId, payout).stream())
      .toList();
  }

  @Mapping(target = "amount", source = "payer.amount")
  @Mapping(target = "personId", source = "payer.personEntries.person.id")
  @Mapping(target = "type", constant = "DEBIT")
  @Mapping(target = "gameId", source = "gameId")
  MoneyTransferEntity map(UUID gameId, Payer payer);

  MoneyTransfer map(MoneyTransferEntity entity);

}
