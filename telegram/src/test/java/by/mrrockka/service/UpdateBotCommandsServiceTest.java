package by.mrrockka.service;

import by.mrrockka.bot.PokerClockAbsSender;
import by.mrrockka.bot.TelegramBotsProperties;
import by.mrrockka.mapper.BotCommandMapper;
import by.mrrockka.service.help.BotDescriptionProperties;
import by.mrrockka.service.help.CommandDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBotCommandsServiceTest {
  private static final String COMMAND = "command";
  @Mock
  private PokerClockAbsSender pokerClockAbsSender;
  @Mock
  private BotCommandMapper botCommandMapper;
  @Mock
  private TelegramBotsProperties telegramBotsProperties;
  @Mock
  private BotDescriptionProperties botDescriptionProperties;
  @InjectMocks
  private UpdateBotCommandsService updateBotCommandsService;

  @Test
  void givenTelegramBotPropertiesAndBotIsDisabled_whenUpdateBotCommandsCalled_thenShouldLog() {
    when(telegramBotsProperties.getEnabled()).thenReturn(false);
    updateBotCommandsService.updateBotCommands();
    verifyNoInteractions(pokerClockAbsSender, botCommandMapper, botDescriptionProperties);
  }

  @Test
  void givenCommandsDescription_whenUpdateBotCommandsCalled_thenShouldUpdateCommands() throws TelegramApiException {
    final var commands = Map.of(COMMAND, CommandDescription.builder().build());
    final var apiCommands = List.of(BotCommand.builder().command(COMMAND).description("").build());

    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    when(botDescriptionProperties.getCommands()).thenReturn(commands);
    when(botCommandMapper.mapToApi(commands)).thenReturn(apiCommands);
    updateBotCommandsService.updateBotCommands();
    verify(pokerClockAbsSender).execute(SetMyCommands.builder().commands(apiCommands).build());
    verifyNoMoreInteractions(pokerClockAbsSender, botCommandMapper, botDescriptionProperties, telegramBotsProperties);
  }

  @Test
  void givenCommandsDescription_whenUpdateBotCommandsCalledAndExceptionThrown_thenShouldLog() throws TelegramApiException {
    final var commands = Map.of(COMMAND, CommandDescription.builder().build());
    final var apiCommands = List.of(BotCommand.builder().command(COMMAND).description("").build());

    when(telegramBotsProperties.getEnabled()).thenReturn(true);
    when(botDescriptionProperties.getCommands()).thenReturn(commands);
    when(botCommandMapper.mapToApi(commands)).thenReturn(apiCommands);
    when(pokerClockAbsSender.execute(SetMyCommands.builder().commands(apiCommands).build())).thenThrow(
      TelegramApiException.class);
    assertThatCode(() -> updateBotCommandsService.updateBotCommands()).doesNotThrowAnyException();
    verifyNoMoreInteractions(pokerClockAbsSender, botCommandMapper, botDescriptionProperties, telegramBotsProperties);
  }

}