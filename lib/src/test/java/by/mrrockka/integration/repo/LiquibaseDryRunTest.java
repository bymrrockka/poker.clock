package by.mrrockka.integration.repo;

import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.repo.entities.GameEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ExtendWith(PostgreSQLExtension.class)
public class LiquibaseDryRunTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void test_changelogs() {
    jdbcTemplate.queryForList("SELECT 1;", GameEntity.class);
  }

}
