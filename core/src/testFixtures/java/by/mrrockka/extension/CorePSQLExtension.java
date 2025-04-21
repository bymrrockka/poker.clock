package by.mrrockka.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static by.mrrockka.extension.TestPSQLContainer.*;

@Slf4j
public class CorePSQLExtension implements BeforeAllCallback, AfterEachCallback {
  @Override
  public void beforeAll(final ExtensionContext context) {
    container.start();
    datasourceProperties();
  }

  private void datasourceProperties() {
    System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:%s:///%s".formatted(VERSION, DB_NAME));
    System.setProperty("spring.datasource.username", container.getUsername());
    System.setProperty("spring.datasource.password", container.getPassword());
  }

  @Override
  public void afterEach(final ExtensionContext context) {
//    todo: generate data for ITests instead of prepopulating throughout migration scripts
//    final var jdbcTemplate = SpringExtension.getApplicationContext(context).getBean(NamedParameterJdbcTemplate.class);
//    jdbcTemplate.execute("truncate bounty", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate entries", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate finale_places", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate game", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate money_transfer", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate person", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate poll_task", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate prize_pool", PreparedStatement::execute);
//    jdbcTemplate.execute("truncate withdrawal", PreparedStatement::execute);
  }


}
