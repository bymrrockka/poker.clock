package by.mrrockka.jobexecution.schedulers;

import by.mrrockka.jobexecution.repository.PollsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PollsTelegramServiceTest {

  @Mock
  private PollsRepository pollsRepository;
  @InjectMocks
  private PollsTelegramService pollsTelegramService;

  @Test
  void whenProcessExecuted_thenShouldGetAllTheJobsAndSendMessages() {
    pollsTelegramService.process();
    verify(pollsRepository).getAll();
  }
}