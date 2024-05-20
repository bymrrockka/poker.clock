package by.mrrockka.service;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.domain.Person;
import by.mrrockka.repo.person.PersonRepository;
import by.mrrockka.repo.person.TelegramPersonEntity;
import by.mrrockka.repo.person.TelegramPersonRepository;
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
@ActiveProfiles("repository")
class TelegramPersonServiceTest {

  private static final Long CHAT_ID = 123L;
  @Autowired
  private TelegramPersonService telegramPersonService;
  @Autowired
  private PersonRepository personRepository;
  @Autowired
  private TelegramPersonRepository telegramPersonRepository;

  private static Stream<Arguments> nicknames() {
    return Stream.of(
      Arguments.of(
        List.of("mrrocka", "andrei", "marks")
      ),
      Arguments.of(
        List.of("mrrocka", "kinger", "queen")
      )
    );
  }

  @ParameterizedTest
  @MethodSource("nicknames")
  void givenMessageWithNicknames_whenAttemptToStoreAndSomeTelegramsExistsInDb_shouldStoreOnlyNewPersons(
    final List<String> args) {
    final var nicknames = new ArrayList<>(args);

    final var text = nicknames.stream()
      .map("@%s"::formatted)
      .reduce("%s\n%s"::formatted)
      .orElseThrow();

    final var messageMetadata = MessageMetadataCreator.domain(metadata -> metadata
      .chatId(CHAT_ID)
      .text(text)
      .entities(args.stream()
                  .map(MessageEntityCreator::domainMention)
                  .toList())
    );

    final var personIds = telegramPersonService.storePersons(messageMetadata).stream()
      .map(Person::getId)
      .toList();
    assertThat(personRepository.findAllByIds(personIds)).hasSize(args.size());

    final var actualIds = telegramPersonRepository.findAllByChatIdAndNicknames(CHAT_ID, nicknames).stream()
      .map(TelegramPersonEntity::getId)
      .toList();

    assertThat(actualIds).containsAll(personIds);
  }
}