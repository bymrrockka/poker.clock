package by.mrrockka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"by.mrrockka.listener", "org.telegram.telegrambots"})
public class TelegramApiBotApp {

  public static void main(String[] args) {
    SpringApplication.run(TelegramApiBotApp.class, args);
  }
}
