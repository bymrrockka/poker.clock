package by.mrrockka.repo.person;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.extension.CorePSQLExtension;
import by.mrrockka.creator.PersonCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CorePSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class PersonRepositoryTest {

  @Autowired
  PersonRepository personRepository;

  @Test
  void givenPersonEntity_whenSave_thenShouldBeAbleToGet() {
    final var expected = PersonCreator.entityRandom();

    personRepository.save(expected);

    assertThat(personRepository.findById(expected.getId())).isEqualTo(expected);
  }

  @Test
  void givenNickname_whenGetByNicknameCalled_thenShouldReturnPersonEntity() {
    final var expected = PersonCreator.entityRandom();

    personRepository.save(expected);

    assertThat(personRepository.findByNickname(expected.getNickname())).isEqualTo(expected);
  }

  @Test
  void givenPersonEntityList_whenSave_thenShouldBeAbleToGet() {
    final var expected = List.of(PersonCreator.entityRandom());

    personRepository.saveAll(expected);

    assertThat(personRepository.findAllByIds(List.of(expected.get(0).getId()))).isEqualTo(expected);
  }

  @Test
  void givenPersonEntities_whenetNewNicknames_thenShouldBeAbleToFind() {
    final var randomPersons = IntStream.range(0, 3)
      .mapToObj(i -> PersonCreator.entityRandom())
      .toList();

    final var personNicknames = randomPersons.stream().map(PersonEntity::getNickname).toList();
    personRepository.saveAll(randomPersons);

    assertThat(
      personRepository.findNewNicknames(randomPersons.stream().map(PersonEntity::getNickname).toList())
        .containsAll(personNicknames));
  }

}