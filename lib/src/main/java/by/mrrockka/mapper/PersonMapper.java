package by.mrrockka.mapper;

import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PersonMapper {

  PersonEntity toEntity(Person domain, String chatId);


  default List<PersonEntity> toEntities(List<Person> domains, String chatId) {
    return domains.stream()
      .map(domain -> toEntity(domain, chatId))
      .toList();
  }

  Person toDomain(PersonEntity entity);

  List<Person> toDomains(List<PersonEntity> entity);

}
