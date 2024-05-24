package by.mrrockka.mapper;

import by.mrrockka.domain.payout.Payer;
import by.mrrockka.domain.payout.TransferType;
import by.mrrockka.repo.moneytransfer.MoneyTransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(imports = TransferType.class)
public interface PersonGlobalStatisticsMapper {
  //todo:
  @Mapping(target = "amount", source = "payer.amount")
  @Mapping(target = "personId", source = "payer.personEntries.person.id")
  @Mapping(target = "type", constant = "DEBIT")
  @Mapping(target = "gameId", source = "gameId")
  MoneyTransferEntity map(UUID gameId, Payer payer);

}
