package by.mrrockka.repo.person;

import by.mrrockka.IntegrationTestConfiguration;
import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.PersonCreator;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest(classes = IntegrationTestConfiguration.class)
class PersonRepositoryTest {

  @Autowired
  PersonRepository personRepository;

  @Builder
  private record PersonArgument(String telegram, String firstname, String lastname, String chatId) {}

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

  static Stream<Arguments> telegramArguments() {
    return Stream.of(
      Arguments.of(
        PersonArgument.builder()
          .telegram("jack")
          .firstname("Grisha")
          .lastname("Anikii")
          .chatId("123")
          .build()
      ),
      Arguments.of(
        PersonArgument.builder()
          .telegram("queen")
          .firstname("Kate")
          .chatId("123")
          .build()
      ),
      Arguments.of(
        PersonArgument.builder()
          .telegram("king")
          .lastname("Portugal")
          .chatId("123")
          .build()
      ),
      Arguments.of(
        PersonArgument.builder()
          .telegram("ace")
          .chatId("123")
          .build()
      )
    );
  }
/* todo: move to service
  @ParameterizedTest
  @MethodSource("telegramArguments")
  void givenTestData_whenFindByTelegramExecuted_thenShouldReturnValidEntities(PersonArgument argument) {
    assertThat(personRepository.findByTelegram(argument.telegram(), argument.chatId()))
      .usingRecursiveComparison()
      .ignoringExpectedNullFields()
      .isEqualTo(argument);
  }

  @Test
  void givenTestData_whenFindAllByTelegramExecuted_thenShouldGetAllRelatedEntities() {
    final var personArguments = telegramArguments()
      .map(Arguments::get)
      .map(objects -> (PersonArgument) objects[0])
      .toList();

    final var chatId = personArguments.stream()
      .findFirst()
      .map(PersonArgument::chatId)
      .orElseThrow();
    final var telegrams = personArguments.stream()
      .map(PersonArgument::telegram)
      .toList();


    assertThat(personRepository.findAllByTelegrams(telegrams, chatId))
      .usingRecursiveComparison()
      .ignoringExpectedNullFields()
      .ignoringFields("id")
      .ignoringCollectionOrder()
      .isEqualTo(personArguments);
  }

  @Test
  void givenPersonList_whenAllStored_shouldBeAbleToGetAllByTelegrams() {
    final var chatId = new Faker().random().hex();
    final Consumer<PersonEntity.PersonEntityBuilder> personBuilder =
      builder -> builder
        .id(UUID.randomUUID())
        .chatId(chatId)
        .telegram(FakerProvider.faker().funnyName().name());



    final var listExpected = List.of(
      PersonCreator.entity(personBuilder),
      PersonCreator.entity(personBuilder),
      PersonCreator.entity(personBuilder),
      PersonCreator.entity(personBuilder),
      PersonCreator.entity(personBuilder),
      PersonCreator.entity(personBuilder)
    );

    final var telegrams = listExpected.stream()
      .map(PersonEntity::telegram)
      .toList();

    personRepository.saveAll(listExpected);

    assertThat(personRepository.findAllByTelegrams(telegrams, chatId))
      .containsExactlyInAnyOrderElementsOf(listExpected);
  }*/
}