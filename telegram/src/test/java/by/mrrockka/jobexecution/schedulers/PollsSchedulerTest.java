package by.mrrockka.jobexecution.schedulers;

import by.mrrockka.jobexecution.service.PollsScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PollsSchedulerTest {

  @Mock
  private PollsTelegramService pollsTelegramService;
  @InjectMocks
  private PollsScheduler pollsScheduler;

  @Test
  void whenCalled_thenShouldProcessJobs() {
    pollsScheduler.execute();

    verify(pollsTelegramService).process();
  }


}