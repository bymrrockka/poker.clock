package by.mrrockka.config;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {
  @Override
  public void beforeAll(ExtensionContext context) {
    TestPostgreSQLContainer.container.start();
    datasourceProperties();
  }

  private void datasourceProperties() {
    System.setProperty("spring.datasource.url", TestPostgreSQLContainer.container.getJdbcUrl());
    System.setProperty("spring.datasource.username", TestPostgreSQLContainer.container.getUsername());
    System.setProperty("spring.datasource.password", TestPostgreSQLContainer.container.getPassword());
  }

  @Override
  public void afterAll(ExtensionContext context) {
  }
}
