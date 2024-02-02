package by.mrrockka.integration.repo;

import by.mrrockka.integration.repo.config.PostgreSQLExtension;
import by.mrrockka.repo.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(PostgreSQLExtension.class)
@SpringBootTest
class PersonRepositoryTest {

  @Autowired
  PersonRepository personRepository;

  @Test
  void test() {

  }
}