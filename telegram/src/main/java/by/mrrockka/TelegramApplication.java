package by.mrrockka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"by.mrrockka", "org.telegram.telegrambots"})
public class TelegramApplication {

  public static void main(final String[] args) {
    SpringApplication.run(TelegramApplication.class, args);
  }
}
