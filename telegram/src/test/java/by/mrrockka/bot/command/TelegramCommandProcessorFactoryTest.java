package by.mrrockka.bot.command;

import by.mrrockka.bot.TelegramBotsProperties;
import by.mrrockka.bot.command.processor.CalculateTelegramCommandProcessor;
import by.mrrockka.bot.command.processor.EntryTelegramCommandProcessor;
import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.bot.command.processor.game.BountyGameTelegramCommandProcessor;
import by.mrrockka.creator.MessageEntityCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.service.help.BotDescriptionProperties;
import by.mrrockka.service.help.CommandDescription;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramCommandProcessorFactoryTest {

  private static final String BOTNAME = "botname";
  private static final String DESCRIPTION = "description";
  private static final String DETAILS = "details";

  @Mock
  private BotDescriptionProperties botDescriptionProperties;
  @Mock
  private TelegramBotsProperties telegramBotsProperties;
  @Mock
  private ApplicationContext applicationContext;
  @InjectMocks
  private TelegramCommandProcessorFactory telegramCommandProcessorFactory;

  private static Stream<Arguments> commands() {
    return Stream.of(
      Arguments.of(
        "bounty_game",
        new BountyGameTelegramCommandProcessor(null)
      ),
      Arguments.of(
        "/bounty_game",
        new BountyGameTelegramCommandProcessor(null)
      ),
      Arguments.of(
        "/entry",
        new EntryTelegramCommandProcessor(null)
      ),
      Arguments.of(
        "/entry@%s".formatted(BOTNAME),
        new EntryTelegramCommandProcessor(null)
      ),
      Arguments.of(
        "/calculate",
        new CalculateTelegramCommandProcessor(null)
      ),
      Arguments.of(
        "/calculate@%s".formatted(BOTNAME),
        new CalculateTelegramCommandProcessor(null)
      )
    );
  }

  @ParameterizedTest
  @MethodSource("commands")
  void givenMessageMetadata_whenProvideProcessorInvoked_thenShouldReturnStrategy(final String command,
                                                                                 final TelegramCommandProcessor commandProcessor) {
    final var metadata = MessageMetadataCreator.domain(
      builder ->
        builder.entities(
          List.of(MessageEntityCreator.domainCommand(command))
        ));

    final var commandName = command.replaceAll("/", "").replaceAll("@" + BOTNAME, "");

    final var commandDescription = CommandDescription.builder()
      .description(DESCRIPTION)
      .enabled(true)
      .details(DETAILS)
      .build();

    final var commands = Map.of(commandName, commandDescription);

    when(telegramBotsProperties.getNickname()).thenReturn(BOTNAME);
    when(botDescriptionProperties.getCommands()).thenReturn(commands);
    when(applicationContext.getBean(StringUtils.uncapitalize(commandProcessor.getClass().getSimpleName()),
                                    TelegramCommandProcessor.class)).thenReturn(commandProcessor);

    assertThat(telegramCommandProcessorFactory.provideProcessor(metadata)).isEqualTo(commandProcessor);
  }

  @Test
  void givenMessageMetadataWithNotExistentCommand_whenProvideProcessorInvoked_thenShouldThrowException() {
    final var command = "command";
    final var metadata = MessageMetadataCreator.domain(
      builder ->
        builder.entities(
          List.of(MessageEntityCreator.domainCommand(command))
        ));

    when(telegramBotsProperties.getNickname()).thenReturn(BOTNAME);
    when(botDescriptionProperties.getCommands()).thenReturn(Map.of());

    assertThatThrownBy(() -> telegramCommandProcessorFactory.provideProcessor(metadata))
      .isInstanceOf(NoCommandProcessorFoundException.class)
      .hasMessage("No command processors found.");
  }

  @Test
  void givenMessageMetadata_whenProvideProcessorInvokedAndCommandIsDisabled_thenShouldThrowException() {
    final var command = "command";
    final var metadata = MessageMetadataCreator.domain(
      builder ->
        builder.entities(
          List.of(MessageEntityCreator.domainCommand(command))
        ));

    final var commandDescription = CommandDescription.builder()
      .description(DESCRIPTION)
      .enabled(false)
      .details(DETAILS)
      .build();

    final var commands = Map.of(command, commandDescription);

    when(telegramBotsProperties.getNickname()).thenReturn(BOTNAME);
    when(botDescriptionProperties.getCommands()).thenReturn(commands);

    assertThatThrownBy(() -> telegramCommandProcessorFactory.provideProcessor(metadata))
      .isInstanceOf(CommandProcessingIsDisabledException.class);
  }

  @Test
  void givenMessageMetadata_whenProvideProcessorInvokedAndThereIsNoCommandProcessorImplemented_thenShouldThrowException() {
    final var command = "command";
    final var commandProcessorBeanName = "commandTelegramCommandProcessor";
    final var metadata = MessageMetadataCreator.domain(
      builder ->
        builder.entities(
          List.of(MessageEntityCreator.domainCommand(command))
        ));

    final var commandDescription = CommandDescription.builder()
      .description(DESCRIPTION)
      .enabled(true)
      .details(DETAILS)
      .build();

    final var commands = Map.of(command, commandDescription);

    when(telegramBotsProperties.getNickname()).thenReturn(BOTNAME);
    when(botDescriptionProperties.getCommands()).thenReturn(commands);

    when(applicationContext.getBean(commandProcessorBeanName, TelegramCommandProcessor.class))
      .thenThrow(NoSuchBeanDefinitionException.class);

    assertThatThrownBy(() -> telegramCommandProcessorFactory.provideProcessor(metadata))
      .isInstanceOf(NoCommandProcessorFoundException.class)
      .hasMessage("No command processors found for command command.");
  }
}