package by.mrrockka.config;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    TestPostgreSQLContainer.container.start();
    datasourceProperties(TestPostgreSQLContainer.container);
  }

  private void datasourceProperties(TestPostgreSQLContainer container) {
    System.setProperty("spring.datasource.url", container.getJdbcUrl());
    System.setProperty("spring.datasource.username", container.getUsername());
    System.setProperty("spring.datasource.password", container.getPassword());
  }

  @Override
  public void afterAll(ExtensionContext context) {
  }
}
