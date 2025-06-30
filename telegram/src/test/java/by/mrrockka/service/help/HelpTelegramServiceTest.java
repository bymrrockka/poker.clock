package by.mrrockka.service.help;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.parser.HelpMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelpTelegramServiceTest {
  private static final String HELP_COMMAND = "help";
  private static final String COMMAND = "command";
  private static final String COMMAND_DESCRIPTION = "description";
  private static final String COMMAND_DETAILS = "details";

  @Mock
  private BotDescriptionProperties botDescriptionProperties;
  @Mock
  private HelpMessageParser helpMessageParser;
  @InjectMocks
  private HelpTelegramService helpTelegramService;

  @Test
  void givenHelpWithCommandMessage_whenSendHelpInformationCalled_shouldReturnValidDescription() {
    final var metadata = MessageMetadataCreator.domain();
    final var helpCommandOpt = Optional.of(COMMAND);
    final var commandsMap = Map.of(COMMAND,
                                   CommandDescription.builder()
                                     .description(COMMAND_DESCRIPTION)
                                     .details(COMMAND_DETAILS)
                                     .build());

    when(helpMessageParser.parse(metadata)).thenReturn(helpCommandOpt);
    when(botDescriptionProperties.getCommands()).thenReturn(commandsMap);

    final var actual = (SendMessage) helpTelegramService.sendHelpInformation(metadata);

    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual.getText()).isEqualTo(COMMAND_DETAILS),
      () -> assertThat(actual.getChatId()).isEqualTo(String.valueOf(metadata.getChatId()))
    );
  }

  @Test
  void givenHelpWithoutCommandMessage_whenSendHelpInformationCalled_shouldReturnHelpDescription() {
    final var metadata = MessageMetadataCreator.domain();
    final var commandsMap = Map.of(HELP_COMMAND,
                                   CommandDescription.builder()
                                     .description(COMMAND_DESCRIPTION)
                                     .details(COMMAND_DETAILS)
                                     .build());

    when(helpMessageParser.parse(metadata)).thenReturn(Optional.empty());
    when(botDescriptionProperties.getCommands()).thenReturn(commandsMap);

    final var actual = (SendMessage) helpTelegramService.sendHelpInformation(metadata);

    assertAll(
      () -> assertThat(actual).isNotNull(),
      () -> assertThat(actual.getText()).isEqualTo(COMMAND_DETAILS),
      () -> assertThat(actual.getChatId()).isEqualTo(String.valueOf(metadata.getChatId()))
    );
  }
}