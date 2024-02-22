package by.mrrockka.route;

import by.mrrockka.service.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TournamentCommandRoute implements CommandRoute {
  private static final String COMMAND = "/tournament";
  private final TelegramGameService gameService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return gameService.storeGame(update);
  }

  @Override
  public boolean isApplicable(final Update update) {
    return CommandRoute.super.isApplicable(update) &&
      update.getMessage().getText().contains(COMMAND);
  }


}
