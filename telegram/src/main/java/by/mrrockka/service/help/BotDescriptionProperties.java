package by.mrrockka.service.help;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "botdescription")
@RequiredArgsConstructor
@Getter
public class BotDescriptionProperties {

  private final Map<String, CommandDescription> commands;
}
