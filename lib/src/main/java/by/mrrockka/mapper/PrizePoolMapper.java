package by.mrrockka.mapper;

import by.mrrockka.domain.prize.PositionAndPercentage;
import by.mrrockka.domain.prize.PrizePool;
import by.mrrockka.repo.prizepool.PrizePoolEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Mapper
public interface PrizePoolMapper {

  @Mapping(target = "schema", source = "domain.positionAndPercentages", qualifiedByName = "mapToSchema")
  PrizePoolEntity toEntity(UUID gameId, PrizePool domain);

  @Mapping(target = "positionAndPercentages", source = "schema", qualifiedByName = "mapToPositionAndPercentages")
  PrizePool toDomain(PrizePoolEntity entity);

  @Named("mapToSchema")
  default Map<Integer, BigDecimal> mapToSchema(final List<PositionAndPercentage> positionAndPercentages) {
    return positionAndPercentages.stream()
      .map(pp -> new AbstractMap.SimpleEntry<>(pp.position(), pp.percentage()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Named("mapToPositionAndPercentages")
  default List<PositionAndPercentage> mapToPositionAndPercentages(final Map<Integer, BigDecimal> schema) {
    return schema.entrySet()
      .stream()
      .map(entry -> PositionAndPercentage.builder()
        .position(entry.getKey())
        .percentage(entry.getValue())
        .build())
      .sorted(Comparator.comparingLong(PositionAndPercentage::position))
      .toList();
  }
}
