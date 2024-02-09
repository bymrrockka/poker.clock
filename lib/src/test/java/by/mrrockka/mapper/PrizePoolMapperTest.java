package by.mrrockka.mapper;

import by.mrrockka.creator.PrizePoolCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PrizePoolMapperTest {

  PrizePoolMapper prizePoolMapper = Mappers.getMapper(PrizePoolMapper.class);

  @Test
  void givenEntity_whenMapExecuted_shouldReturnDomain() {
    assertThat(prizePoolMapper.toDomain(PrizePoolCreator.prizePoolEntity()))
      .isEqualTo(PrizePoolCreator.prizePool());
  }

  @Test
  void givenDomain_whenMapExecuted_shouldReturnEntity() {
    assertThat(prizePoolMapper.toEntity(PrizePoolCreator.GAME_ID, PrizePoolCreator.prizePool()))
      .isEqualTo(PrizePoolCreator.prizePoolEntity());
  }

}