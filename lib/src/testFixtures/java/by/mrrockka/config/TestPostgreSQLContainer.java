package by.mrrockka.config;

import org.testcontainers.containers.PostgreSQLContainer;

class TestPostgreSQLContainer extends PostgreSQLContainer<TestPostgreSQLContainer> {

  private static final String IMAGE_VERSION = "postgres:16.1";
  private static final String DB_NAME = "pokerclock";
  private static final String USERNAME = "itest";
  private static final String PASSWORD = "itest123";

  final static TestPostgreSQLContainer container = new TestPostgreSQLContainer()
    .withDatabaseName(DB_NAME)
    .withUsername(USERNAME)
    .withPassword(PASSWORD);

  TestPostgreSQLContainer() {
    super(IMAGE_VERSION);
  }

}
