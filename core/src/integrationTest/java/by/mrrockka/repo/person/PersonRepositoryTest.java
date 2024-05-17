package by.mrrockka.repo.person;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.PersonCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class PersonRepositoryTest {

  @Autowired
  PersonRepository personRepository;

  @Test
  void givenPersonEntity_whenSave_thenShouldBeAbleToGet() {
    final var expected = PersonCreator.entity();

    personRepository.save(expected);

    assertThat(personRepository.findById(expected.getId())).isEqualTo(expected);
  }

  @Test
  void givenPersonEntityList_whenSave_thenShouldBeAbleToGet() {
    final var expected = List.of(PersonCreator.entity(builder -> builder.id(UUID.randomUUID())));

    personRepository.saveAll(expected);

    assertThat(personRepository.findAllByIds(List.of(expected.get(0).getId()))).isEqualTo(expected);
  }

}