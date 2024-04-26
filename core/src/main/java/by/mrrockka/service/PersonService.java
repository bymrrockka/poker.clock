package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;
  private final PersonMapper personMapper;

  @Transactional(propagation = Propagation.REQUIRED)
  public void storeAll(final List<Person> persons) {
    personRepository.saveAll(personMapper.toEntities(persons));
  }

  public void store(final Person person) {
    personRepository.save(personMapper.toEntity(person));
  }

}
