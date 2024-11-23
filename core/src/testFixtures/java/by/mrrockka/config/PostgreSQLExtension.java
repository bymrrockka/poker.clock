package by.mrrockka.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.PreparedStatement;

import static by.mrrockka.config.TestPostgreSQLContainer.*;

@Slf4j
public class PostgreSQLExtension implements BeforeAllCallback, AfterEachCallback {
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
  public void afterEach(ExtensionContext context) {
    final var jdbcTemplate = SpringExtension.getApplicationContext(context).getBean(NamedParameterJdbcTemplate.class);
    jdbcTemplate.execute("truncate bounty", PreparedStatement::execute);
  }


}
