package by.mrrockka.mapper;

import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PersonMapper {

  PersonEntity toEntity(Person domain);

  Person toDomain(PersonEntity entity);

}
