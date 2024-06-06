package by.mrrockka.bot.command;

import by.mrrockka.bot.TelegramBotsProperties;
import by.mrrockka.bot.command.processor.TelegramCommandProcessor;
import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.service.help.BotDescriptionProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class TelegramCommandProcessorFactory {

  private final BotDescriptionProperties botDescriptionProperties;
  private final TelegramBotsProperties telegramBotsProperties;
  private final ApplicationContext applicationContext;

  public TelegramCommandProcessor provideProcessor(final MessageMetadata messageMetadata) {
    final var commandName = messageMetadata.command()
      .text()
      .toLowerCase()
      .replaceAll("/", "")
      .replaceAll("@%s".formatted(telegramBotsProperties.getNickname()), "");

    final var commandDescription = botDescriptionProperties.getCommands().get(commandName);

    if (isNull(commandDescription)) {
      throw new NoCommandProcessorFoundException();
    }

    if (!commandDescription.enabled()) {
      throw new CommandProcessingIsDisabledException();
    }

    final var interfaceClass = TelegramCommandProcessor.class;
//    todo: cover with try-catch block
    return applicationContext.getBean(assembleProcessorName(commandName, interfaceClass), interfaceClass);
  }

  private String assembleProcessorName(final String commandName, final Class<TelegramCommandProcessor> interfaceClass) {
    final var processorNamePrefix = Arrays.stream(commandName.split("_"))
      .map(StringUtils::capitalize)
      .reduce("%s%s"::formatted)
      .orElse(commandName);

    return StringUtils.uncapitalize(processorNamePrefix) + interfaceClass.getSimpleName();
  }
}
