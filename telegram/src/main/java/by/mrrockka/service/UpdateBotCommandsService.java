package by.mrrockka.service;

import by.mrrockka.bot.PokerClockAbsSender;
import by.mrrockka.bot.properties.CommandDescription;
import by.mrrockka.mapper.BotCommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateBotCommandsService {

  private final PokerClockAbsSender pokerClockAbsSender;
  private final BotCommandMapper botCommandMapper;

  public void updateBotCommands(final Map<String, CommandDescription> descriptions) {
    log.debug("Attempt to update bot commands");
    final var botCommands = botCommandMapper.mapToApi(descriptions);
    log.debug("New commands list %s".formatted(botCommands));
    try {
      pokerClockAbsSender.execute(SetMyCommands.builder().commands(botCommands).build());
    } catch (final TelegramApiException telegramApiException) {
      log.debug("Bot commands update failed", telegramApiException);
    }
    log.debug("Bot commands update completed");
  }

}
