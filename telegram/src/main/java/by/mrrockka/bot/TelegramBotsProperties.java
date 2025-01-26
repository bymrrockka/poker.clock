package by.mrrockka.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegrambots")
@Data
public class TelegramBotsProperties {

  private String name;
  private String nickname;
  private boolean test;
  private String token;
}
