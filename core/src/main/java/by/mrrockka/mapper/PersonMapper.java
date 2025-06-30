package by.mrrockka.mapper;

import by.mrrockka.domain.BasicPerson;
import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PersonMapper {

  PersonEntity toEntity(Person domain);

  List<PersonEntity> toEntities(List<? extends Person> domains);

  default Person toDomain(PersonEntity entity) {
    return new BasicPerson(
      entity.getId(),
      entity.getFirstname(),
      entity.getLastname(),
      entity.getNickname()
    );
  }

  List<Person> toDomains(List<PersonEntity> entity);

}
