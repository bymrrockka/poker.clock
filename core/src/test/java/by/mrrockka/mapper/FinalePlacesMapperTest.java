package by.mrrockka.mapper;

import by.mrrockka.creator.FinalePlacesCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FinalePlacesMapperTest {

  private final FinalePlacesMapper finalePlacesMapper = Mappers.getMapper(FinalePlacesMapper.class);

  @Test
  void givenEntity_whenMapExecuted_shouldReturnDomain() {
    assertThat(finalePlacesMapper.toDomain(FinalePlacesCreator.finalePlacesEntity()))
      .isEqualTo(FinalePlacesCreator.finalePlaces());
  }

  @Test
  void givenDomain_whenMapExecuted_shouldReturnEntity() {
    assertThat(finalePlacesMapper.toEntity(FinalePlacesCreator.GAME_ID, FinalePlacesCreator.finalePlaces()))
      .isEqualTo(FinalePlacesCreator.finalePlacesEntity());
  }

}