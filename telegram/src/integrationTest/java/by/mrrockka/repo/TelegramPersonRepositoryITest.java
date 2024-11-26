package by.mrrockka.repo;

import by.mrrockka.FakerProvider;
import by.mrrockka.config.TelegramPSQLExtension;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.repo.person.PersonEntity;
import by.mrrockka.repo.person.PersonRepository;
import by.mrrockka.repo.person.TelegramPersonEntity;
import by.mrrockka.repo.person.TelegramPersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TelegramPSQLExtension.class)
@SpringBootTest
@ActiveProfiles("repository")
class TelegramPersonRepositoryITest {

  private static final UUID PERSON_ID = UUID.randomUUID();
  private static final Long CHAT_ID = 123L;
  private static final String NICKNAME = "okmasdf";

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private TelegramPersonRepository telegramPersonRepository;

  @Test
  void givenPersonIdAndChatIdAndNickname_whenSaveExecuted_shouldReturnValidEntities() {
    personRepository.save(PersonEntity.personBuilder()
                            .nickname(NICKNAME)
                            .id(PERSON_ID)
                            .build());

    telegramPersonRepository.save(TelegramPerson.telegramPersonBuilder()
                                    .id(PERSON_ID)
                                    .chatId(CHAT_ID)
                                    .nickname(NICKNAME)
                                    .build());
    assertThat(
      telegramPersonRepository.findAllByChatIdAndNicknames(List.of(NICKNAME), CHAT_ID).stream()
        .map(TelegramPersonEntity::getId))
      .contains(PERSON_ID);
  }

  @Test
  void givenChatIdAndNickname_whenFindNotExistentExecuted_shouldReturnValidIds() {
    final var newChatId = FakerProvider.faker().number().randomNumber();
    final var randomPersons = IntStream.range(0, 3)
      .mapToObj(i -> PersonCreator.entityRandom())
      .toList();

    final var personIds = randomPersons.stream().map(PersonEntity::getId).toList();

    personRepository.saveAll(randomPersons);
    telegramPersonRepository.saveAll(personIds, newChatId);

    assertThat(
      telegramPersonRepository.findNotExistentInChat(
          randomPersons.stream().map(PersonEntity::getNickname).toList(), CHAT_ID)
        .containsAll(personIds));
  }

}