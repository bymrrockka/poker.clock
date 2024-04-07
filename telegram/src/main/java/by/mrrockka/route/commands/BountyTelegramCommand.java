package by.mrrockka.route.commands;

import by.mrrockka.service.TelegramBountyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.mrrockka.mapper.CommandRegexConstants.COMMAND_APPENDIX;

@Component
@RequiredArgsConstructor
public class BountyTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "/bounty([\\s]+)%s".formatted(COMMAND_APPENDIX);

  private final TelegramBountyService telegramBountyService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramBountyService.storeBounty(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
