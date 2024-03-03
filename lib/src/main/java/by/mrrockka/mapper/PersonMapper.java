package by.mrrockka.mapper;

import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PersonMapper {

  PersonEntity toEntity(Person domain);

  List<PersonEntity> toEntities(List<Person> domains);

  Person toDomain(PersonEntity entity);

  List<Person> toDomains(List<PersonEntity> entity);

}
