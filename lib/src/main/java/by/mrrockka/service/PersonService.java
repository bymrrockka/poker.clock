package by.mrrockka.service;

import by.mrrockka.domain.Person;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


//todo: add int tests
@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;
  private final PersonMapper personMapper;
//  todo: add logic to store list of persons
//  todo: logic to store one person
//  todo: logic to retrieve all persons by ids
//  todo: logic to retrieve all persons by telegrams
//  todo: logic to retrieve one person by telegram

  public List<Person> retrievePersons(List<UUID> personIds, String chatId) {
    return personMapper.toDomains(personRepository.findAllByIds(personIds, chatId));
  }

  public void storePersons(List<Person> persons, String chatId) {
    personRepository.saveAll(personMapper.toEntities(persons, chatId));
  }

  public void storePerson(Person person, String chatId) {
    personRepository.save(personMapper.toEntity(person, chatId));
  }

}
