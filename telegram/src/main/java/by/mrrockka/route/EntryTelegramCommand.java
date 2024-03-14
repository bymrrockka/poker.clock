package by.mrrockka.route;

import by.mrrockka.service.TelegramEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static by.mrrockka.mapper.CommandRegexConstants.COMMAND_APPENDIX;

@Component
@RequiredArgsConstructor
public class EntryTelegramCommand implements TelegramCommand {
  private static final List<String> COMMANDS = List.of("/entry", "/reentry");

  private final TelegramEntryService telegramEntryService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramEntryService.storeEntry(update);
  }

  @Override
  public boolean isApplicable(final Update update) {
    return TelegramCommand.super.isApplicable(update)
      && COMMANDS.stream()
      .map(str -> str + COMMAND_APPENDIX)
      .anyMatch(command -> update.getMessage().getText().matches(command));
  }
}
