package by.mrrockka.route;

import by.mrrockka.service.TelegramFinalePlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class FinalePlacesCommandRoute implements CommandRoute {
  private static final String COMMAND = "/finaleplaces";

  private final TelegramFinalePlacesService telegramFinalePlacesService;

  @Override
  public BotApiMethodMessage process(Update update) {
    return telegramFinalePlacesService.storePrizePool(update);
  }

  @Override
  public boolean isApplicable(Update update) {
    return CommandRoute.super.isApplicable(update)
      && update.getMessage().getText().contains(COMMAND);
  }
}
