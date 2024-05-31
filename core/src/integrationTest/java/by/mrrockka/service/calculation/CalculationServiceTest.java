package by.mrrockka.service.calculation;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

//@ExtendWith(PostgreSQLExtension.class)
//@SpringBootTest(classes = IntegrationTestConfiguration.class)
public class CalculationServiceTest {

  private static final UUID GAME_ID = UUID.fromString("b759ac52-1496-463f-b0d8-982deeac085c");

  @Test
  void givenGameId_whenCalculatePayoutIsExecuted_shouldCalculatePayoutsAndStoreMoneyTransfers() {
    fail("add test");
  }
}
