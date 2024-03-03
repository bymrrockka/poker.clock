package by.mrrockka.service;

import by.mrrockka.creator.PersonCreator;
import by.mrrockka.mapper.PersonMapper;
import by.mrrockka.repo.person.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

  private static final UUID PERSON_ID = UUID.randomUUID();

  @Mock
  private PersonRepository personRepository;
  @Mock
  private PersonMapper personMapper;
  @InjectMocks
  private PersonService personService;

  @Test
  void givenPersonId_whenRetrievePersonExecuted_shouldReturnPerson() {
    final var entity = PersonCreator.entity();
    final var expected = PersonCreator.domain();
    when(personMapper.toDomain(entity))
      .thenReturn(expected);
    when(personRepository.findById(PERSON_ID))
      .thenReturn(entity);
    final var actual = personService.retrievePerson(PERSON_ID);

    assertThat(actual).contains(expected);
  }

  @Test
  void givenPerson_whenSavePersonsExecuted_shouldCallRepo() {
    final var entity = PersonCreator.entity();
    final var domain = PersonCreator.domain();
    when(personMapper.toEntity(domain))
      .thenReturn(entity);
    personService.store(domain);
    verify(personRepository).save(entity);
  }

  @Test
  void givenPersonList_whenSaveAllPersonsExecuted_shouldCallRepo() {
    final var entities = List.of(PersonCreator.entity());
    final var domains = List.of(PersonCreator.domain());
    when(personMapper.toEntities(domains))
      .thenReturn(entities);
    personService.storeAll(domains);
    verify(personRepository).saveAll(entities);
  }

}