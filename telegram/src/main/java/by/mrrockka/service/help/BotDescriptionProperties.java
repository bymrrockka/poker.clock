package by.mrrockka.service.help;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "bot.description")
@RequiredArgsConstructor
@Getter
public class BotDescriptionProperties {

  private final Map<String, CommandDescription> commands;
}
