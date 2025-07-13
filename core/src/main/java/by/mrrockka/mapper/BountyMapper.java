package by.mrrockka.mapper;

import by.mrrockka.domain.bounty.Bounty;
import by.mrrockka.repo.bounty.BountyEntity;
import org.mapstruct.Mapper;

@Mapper(uses = PersonMapper.class)
@Deprecated(forRemoval = true)
public interface BountyMapper {

  Bounty toDomain(BountyEntity bountyEntity);
}
