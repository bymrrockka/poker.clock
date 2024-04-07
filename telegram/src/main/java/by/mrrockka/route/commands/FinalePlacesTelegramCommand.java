package by.mrrockka.route.commands;

import by.mrrockka.service.TelegramFinalePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.mrrockka.mapper.CommandRegexConstants.COMMAND_APPENDIX;

@Component
@RequiredArgsConstructor
public class FinalePlacesTelegramCommand implements TelegramCommand {
  private static final String COMMAND = "/finaleplaces%s".formatted(COMMAND_APPENDIX);

  private final TelegramFinalePlacesService telegramFinalePlacesService;

  @Override
  public BotApiMethodMessage process(final Update update) {
    return telegramFinalePlacesService.storePrizePool(update);
  }

  @Override
  public String commandPattern() {
    return COMMAND;
  }

}
