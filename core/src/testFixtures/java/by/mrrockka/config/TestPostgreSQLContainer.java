package by.mrrockka.config;

import org.testcontainers.containers.PostgreSQLContainer;

class TestPostgreSQLContainer extends PostgreSQLContainer<TestPostgreSQLContainer> {

  //  todo: found out that testcontainer creates two containers, one with default values another one with overriden - needs investigation
  public static final String VERSION = "16.1";
  public static final String IMAGE_AND_VERSION = "postgres:%s".formatted(VERSION);
  public static final String DB_NAME = "pokerclock";
  private static final String USERNAME = "itest";
  private static final String PASSWORD = "itest123";

  final static TestPostgreSQLContainer container = new TestPostgreSQLContainer()
    .withDatabaseName(DB_NAME)
    .withUsername(USERNAME)
    .withPassword(PASSWORD);

  TestPostgreSQLContainer() {
    super(IMAGE_AND_VERSION);
  }

}
