package by.mrrockka.service;

import by.mrrockka.repo.entries.EntriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntriesServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();
  private static final UUID PERSON_ID = UUID.randomUUID();
  private static final BigDecimal AMOUNT = BigDecimal.TEN;
  private static final LocalDateTime CREATED_AT = LocalDateTime.now();

  @Mock
  private EntriesRepository entriesRepository;

  @InjectMocks
  private EntriesService entriesService;

  @Test
  void givenPersonEntry_whenAttemptToSave_shouldCallRepository() {
    entriesService.storeEntry(GAME_ID, PERSON_ID, AMOUNT, CREATED_AT);
    verify(entriesRepository).save(GAME_ID, PERSON_ID, AMOUNT, CREATED_AT);
  }

}