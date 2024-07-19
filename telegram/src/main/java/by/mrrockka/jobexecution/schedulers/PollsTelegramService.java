package by.mrrockka.jobexecution.schedulers;

import by.mrrockka.jobexecution.repository.PollsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PollsTelegramService {

  private final PollsRepository pollsRepository;

  public void process() {

  }
}
