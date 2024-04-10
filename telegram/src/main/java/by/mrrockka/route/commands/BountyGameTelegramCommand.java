package by.mrrockka.route.commands;

import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.mrrockka.mapper.CommandRegexConstants.COMMAND_APPENDIX;

@Component
@RequiredArgsConstructor
public class BountyGameTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "/bounty_tournament%s".formatted(COMMAND_APPENDIX);
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
