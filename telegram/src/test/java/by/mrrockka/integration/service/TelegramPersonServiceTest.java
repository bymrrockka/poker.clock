package by.mrrockka.integration.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.ChatCreator;
import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.creator.UserCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonRepository;
import by.mrrockka.repo.person.TelegramPersonEntity;
import by.mrrockka.repo.person.TelegramPersonRepository;
import by.mrrockka.service.TelegramPersonService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class TelegramPersonServiceTest {

  private static final Long CHAT_ID = 123L;
  @Autowired
  private TelegramPersonService telegramPersonService;
  @Autowired
  private PersonRepository personRepository;
  @Autowired
  private TelegramPersonRepository telegramPersonRepository;

  private static Stream<Arguments> telegrams() {
    return Stream.of(
      Arguments.of(
        List.of("mrrocka", "andrei", "marks")
      ),
      Arguments.of(
        List.of("mrrocka", "kinger", "queen", "me")
      )
    );
  }

  @ParameterizedTest
  @MethodSource("telegrams")
  void givenMessageWithTelegrams_whenAttemptToStoreAndSomeTelegramsExistsInDb_shouldStoreOnlyNewPersons(
    List<String> args) {
    final var telegrams = new ArrayList<>(args);

    final var text = telegrams.stream()
      .map("@%s"::formatted)
      .reduce("%s\n%s"::formatted)
      .orElseThrow();

    final var update = UpdateCreator.update(
      MessageCreator.message(message -> {
        message.setText(text);
        message.setChat(ChatCreator.chat(CHAT_ID));
      })
    );

    if (telegrams.contains("me")) {
      telegrams.set(3, UserCreator.USER_NAME);
    }

    final var personIds = telegramPersonService.storePersons(update).stream()
      .map(Person::getId)
      .toList();
    assertThat(personRepository.findAllByIds(personIds)).hasSize(args.size());

    final var actualIds = telegramPersonRepository.findAllByChatIdAndTelegrams(CHAT_ID, telegrams).stream()
      .map(TelegramPersonEntity::getId)
      .toList();

    assertThat(actualIds).containsAll(personIds);
  }
}