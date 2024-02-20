package by.mrrockka.mapper;

import by.mrrockka.creator.PrizePoolCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PrizePoolMapperTest {

  PrizePoolMapper prizePoolMapper = Mappers.getMapper(PrizePoolMapper.class);

  @Test
  void givenEntity_whenMapExecuted_shouldReturnDomain() {
    Assertions.assertThat(prizePoolMapper.toDomain(PrizePoolCreator.entity()))
      .isEqualTo(PrizePoolCreator.domain());
  }

  @Test
  void givenDomain_whenMapExecuted_shouldReturnEntity() {
    Assertions.assertThat(prizePoolMapper.toEntity(PrizePoolCreator.GAME_ID, PrizePoolCreator.domain()))
      .isEqualTo(PrizePoolCreator.entity());
  }

}