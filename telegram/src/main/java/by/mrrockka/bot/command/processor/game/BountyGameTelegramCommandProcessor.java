package by.mrrockka.bot.command.processor.game;

import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.game.GameTelegramFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;

@Component
@RequiredArgsConstructor
public class BountyGameTelegramCommandProcessor implements TelegramCommandProcessor {
  private final GameTelegramFacadeService gameService;

  @Override
  public BotApiMethodMessage process(final MessageMetadata messageMetadata) {
    return gameService.storeBountyGame(messageMetadata);
  }

}
