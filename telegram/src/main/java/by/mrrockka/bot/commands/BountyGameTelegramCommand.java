package by.mrrockka.bot.commands;

import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class BountyGameTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "^/bounty_tournament$";
  private final TelegramGameService gameService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return gameService.storeBountyGame(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
