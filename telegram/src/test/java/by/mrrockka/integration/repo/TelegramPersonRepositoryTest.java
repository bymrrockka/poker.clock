package by.mrrockka.integration.repo;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.domain.TelegramPerson;
import by.mrrockka.repo.person.TelegramPersonRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class TelegramPersonRepositoryTest {

  private static final UUID PERSON_ID = UUID.fromString("13b4108e-2dfa-4fea-8b7b-277e1c87d2d8");
  private static final Long CHAT_ID = 123L;
  private static final String TELEGRAM = "nickname";

  @Autowired
  private TelegramPersonRepository personRepository;

  @Test
  void givenPersonIdAndChatIdAndTelegram_whenSaveExecuted_shouldReturnValidIds() {
    personRepository.save(TelegramPerson.builder()
                            .id(PERSON_ID)
                            .chatId(CHAT_ID)
                            .telegram(TELEGRAM)
                            .build());
    assertThat(
      personRepository.findByChatIdAndTelegrams(CHAT_ID, List.of(TELEGRAM)).stream()
        .map(Pair::getKey))
      .contains(PERSON_ID);
  }
}