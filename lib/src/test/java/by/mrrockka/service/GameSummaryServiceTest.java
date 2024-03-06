package by.mrrockka.service;

import by.mrrockka.creator.FinalePlacesCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.domain.summary.FinalePlaceSummary;
import by.mrrockka.domain.summary.GameSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSummaryServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();
  private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(100);
  @Mock
  private FinalePlacesService finalePlacesService;
  @Mock
  private PrizePoolService prizePoolService;

  @InjectMocks
  private GameSummaryService gameSummaryService;

  @Test
  void givenFinalePlacesAndPrizePool_whenAssembleExecuted_shouldReturnValidData() {
    final var finalePlaces = FinalePlacesCreator.finalePlaces();
    final var prizePool = PrizePoolCreator.domain();
    final var expected = gameSummary();

    when(finalePlacesService.getByGameId(GAME_ID))
      .thenReturn(finalePlaces);

    when(prizePoolService.getByGameId(GAME_ID))
      .thenReturn(prizePool);


    final var actual = gameSummaryService.assembleGameSummary(GAME_ID, TOTAL_AMOUNT);
    assertThat(actual)
      .isEqualTo(expected);
  }

//  todo: test exception

  private GameSummary gameSummary() {
    return new GameSummary(List.of(
      FinalePlaceSummary.builder()
        .amount(BigDecimal.valueOf(60))
        .position(1)
        .person(PersonCreator.domain())
        .build(),
      FinalePlaceSummary.builder()
        .amount(BigDecimal.valueOf(30))
        .position(2)
        .person(PersonCreator.domain())
        .build(),
      FinalePlaceSummary.builder()
        .amount(BigDecimal.valueOf(10))
        .position(3)
        .person(PersonCreator.domain())
        .build()
    ));
  }


}