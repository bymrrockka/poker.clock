package by.mrrockka.route.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "telegrambots")
@Data
public class BotProperties {

  private String name;
  private String nickname;
  private boolean enabled;
  private String token;
  private Map<String, CommandDescription> commands;
}
