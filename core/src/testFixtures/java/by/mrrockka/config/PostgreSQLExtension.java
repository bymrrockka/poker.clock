package by.mrrockka.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static by.mrrockka.config.TestPostgreSQLContainer.*;

@Slf4j
public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    container.start();
    datasourceProperties();
  }

  private void datasourceProperties() {
    System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:%s:///%s".formatted(VERSION, DB_NAME));
    System.setProperty("spring.datasource.username", container.getUsername());
    System.setProperty("spring.datasource.password", container.getPassword());
  }

  @Override
  public void afterAll(ExtensionContext context) {
  }
}
