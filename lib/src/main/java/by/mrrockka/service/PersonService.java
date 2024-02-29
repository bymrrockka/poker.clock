package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;
  private final PersonMapper personMapper;

  public Optional<Person> retrievePerson(UUID personId) {
    return Optional.ofNullable(personMapper.toDomain(personRepository.findById(personId)));
  }

  public List<Person> retrievePersons(List<UUID> personIds) {
    return personMapper.toDomains(personRepository.findAllByIds(personIds));
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void storeAll(List<Person> persons) {
    personRepository.saveAll(personMapper.toEntities(persons));
  }

  public void store(Person person) {
    personRepository.save(personMapper.toEntity(person));
  }

}
