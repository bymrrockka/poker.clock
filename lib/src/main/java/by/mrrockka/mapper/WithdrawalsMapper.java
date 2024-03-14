package by.mrrockka.mapper;

import by.mrrockka.domain.collection.PersonWithdrawals;
import by.mrrockka.repo.withdrawals.WithdrawalsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = PersonMapper.class)
public interface WithdrawalsMapper {

  @Mapping(target = "withdrawals", source = "amounts")
  PersonWithdrawals toDomain(WithdrawalsEntity withdrawalsEntity);
}
