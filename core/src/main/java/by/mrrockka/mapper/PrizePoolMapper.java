package by.mrrockka.mapper;

import by.mrrockka.domain.prize.PositionPrize;
import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.repo.prizepool.PrizePoolEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public interface PrizePoolMapper {

  @Mapping(target = "schema", source = "domain.positionPrizes", qualifiedByName = "mapToSchema")
  PrizePoolEntity toEntity(UUID gameId, PrizePool domain);

  @Mapping(target = "positionPrizes", source = "schema", qualifiedByName = "mapToPositionPrizes")
  PrizePool toDomain(PrizePoolEntity entity);

  @Named("mapToSchema")
  default Map<Integer, BigDecimal> mapToSchema(final List<PositionPrize> positionPrizes) {
    return positionPrizes.stream()
      .collect(Collectors.toMap(PositionPrize::position, PositionPrize::percentage));
  }

  @Named("mapToPositionPrizes")
  default List<PositionPrize> mapToPositionAndPercentages(final Map<Integer, BigDecimal> schema) {
    return schema.entrySet()
      .stream()
      .map(entry -> PositionPrize.builder()
        .position(entry.getKey())
        .percentage(entry.getValue())
        .build())
      .sorted(Comparator.comparingLong(PositionPrize::position))
      .toList();
  }
}
