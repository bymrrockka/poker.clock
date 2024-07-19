package by.mrrockka.jobexecution.service;

import by.mrrockka.jobexecution.schedulers.PollsTelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PollsScheduler {
  private final PollsTelegramService pollsTelegramService;

  @Scheduled(cron = "* * 12 * * *")
  public void execute() {
    pollsTelegramService.process();
  }

}
