package by.mrrockka.mapper;

import by.mrrockka.domain.Player;
import by.mrrockka.domain.entries.Entries;
import by.mrrockka.repo.entries.EntriesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(uses = PersonMapper.class)
public interface PlayerMapper {

  @Mapping(target = "entries", source = "amounts", qualifiedByName = "amountListToPayments")
  Player toPlayer(EntriesEntity entriesEntity);

  @Named("amountListToPayments")
  default Entries amountListToPayments(final List<BigDecimal> amount) {
    return new Entries(null, amount);
  }
}
