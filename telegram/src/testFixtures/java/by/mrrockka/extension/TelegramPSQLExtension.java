package by.mrrockka.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.PreparedStatement;

public class TelegramPSQLExtension extends CorePSQLExtension {

  @Override
  public void afterEach(final ExtensionContext context) {
//    todo: generate data instead of migration scripts usage
    final var jdbcTemplate = SpringExtension.getApplicationContext(context).getBean(NamedParameterJdbcTemplate.class);
    jdbcTemplate.execute("truncate chat_persons", PreparedStatement::execute);
    jdbcTemplate.execute("truncate chat_games", PreparedStatement::execute);
    jdbcTemplate.execute("truncate poll_task", PreparedStatement::execute);
  }
}
