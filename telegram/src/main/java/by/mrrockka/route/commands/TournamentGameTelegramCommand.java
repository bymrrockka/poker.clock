package by.mrrockka.route.commands;

import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TournamentGameTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/tournament$";
  private final TelegramGameService gameService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return gameService.storeTournamentGame(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
