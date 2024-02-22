package by.mrrockka.integration.repo;

import by.mrrockka.config.PostgreSQLExtension;
import by.mrrockka.repo.person.TelegramPersonRepository;
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
  private static final String CHAT_ID = "123";
  private static final String TELEGRAM = "nickname";

  @Autowired
  private TelegramPersonRepository personRepository;

  @Test
  void givenPersonIdAndChatIdAndTelegram_whenSaveExecuted_shouldReturnValidIds() {
    personRepository.save(PERSON_ID, CHAT_ID, TELEGRAM);
    assertThat(personRepository.findByChatIdAndTelegrams(CHAT_ID, List.of(TELEGRAM)))
      .contains(PERSON_ID);
  }
}