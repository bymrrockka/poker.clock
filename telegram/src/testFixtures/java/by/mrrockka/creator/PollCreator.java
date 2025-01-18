package by.mrrockka.creator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PollCreator {
  public static final String POLL_ID = randomPollId();

  public static PollAnswer pollAnswer() {
    return pollAnswer(null);
  }

  public static PollAnswer pollAnswer(final Consumer<PollAnswer> pollAnswerConsumer) {
    final var pollAnswer = new PollAnswer();
    pollAnswer.setPollId(POLL_ID);
    pollAnswer.setUser(UserCreator.user());
    pollAnswer.setOptionIds(List.of(0));

    if (nonNull(pollAnswerConsumer)) {
      pollAnswerConsumer.accept(pollAnswer);
    }

    return pollAnswer;
  }

  public static String randomPollId() {
    return Double.valueOf(100 + Math.random() * 100).toString();
  }

}
