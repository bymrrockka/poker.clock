package by.mrrockka;

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@ComponentScan({"by.mrrockka", "org.telegram.telegrambots"})
@ImportAutoConfiguration(ExposedAutoConfiguration.class)
public class TelegramApplication {

  public static void main(final String[] args) {
    SpringApplication.run(TelegramApplication.class, args);
  }
}
