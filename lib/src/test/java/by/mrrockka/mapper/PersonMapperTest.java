package by.mrrockka.mapper;

import by.mrrockka.creator.PersonCreator;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
    assertThat(personMapper.toEntity(PersonCreator.domain(), PersonCreator.CHAT_ID))
      .isEqualTo(PersonCreator.entity());
  }

}