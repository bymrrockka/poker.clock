package by.mrrockka.mapper;

import by.mrrockka.creator.PersonCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonMapperTest {

  PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

  @Test
  void givenEntity_whenMapExecuted_thenShouldReturnDomain() {
    assertThat(personMapper.toDomain(PersonCreator.entity()))
      .isEqualTo(PersonCreator.domain());
  }

  @Test
  void givenDomain_whenMapExecuted_thenShouldReturnEntity() {
    assertThat(personMapper.toEntity(PersonCreator.domain()))
      .isEqualTo(PersonCreator.entity());
  }

  @Test
  void givenEntityList_whenMapExecuted_thenShouldReturnDomainList() {
    assertThat(personMapper.toDomains(List.of(PersonCreator.entity())))
      .isEqualTo(List.of(PersonCreator.domain()));
  }

  @Test
  void givenDomainList_whenMapExecuted_thenShouldReturnEntityList() {
    assertThat(personMapper.toEntities(List.of(PersonCreator.domain())))
      .isEqualTo(List.of(PersonCreator.entity()));
  }

}