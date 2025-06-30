package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;
  private final PersonMapper personMapper;

  public void storeAll(final List<? extends Person> persons) {
    personRepository.saveAll(personMapper.toEntities(persons));
  }

  public void store(final Person person) {
    personRepository.save(personMapper.toEntity(person));
  }

  public Person getByNickname(final String nickname) {
    return personMapper.toDomain(personRepository.findByNickname(nickname));
  }

  public List<String> getNewNicknames(final List<String> nicknames) {
    return personRepository.findNewNicknames(nicknames);
  }

}
