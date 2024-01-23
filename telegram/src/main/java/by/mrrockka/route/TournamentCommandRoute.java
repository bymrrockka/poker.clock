package by.mrrockka.route;

import by.mrrockka.mapper.game.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TournamentCommandRoute implements CommandRoute {
  private static final String COMMAND = "/tournament";

  private final GameMapper gameMapper;

  @Override
  public BotApiMethodMessage process(final Update update) {
    final var command = update.getMessage().getText()
      .replaceFirst("@me", "@" + update.getMessage().getFrom().getUserName());
    final var game = gameMapper.map(command);


    return null;
  }

  @Override
  public boolean isApplicable(final Update update) {
    return CommandRoute.super.isApplicable(update) &&
      update.getMessage().getText().contains(COMMAND);
  }


}
