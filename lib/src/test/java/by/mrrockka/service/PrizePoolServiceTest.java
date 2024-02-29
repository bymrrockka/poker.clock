package by.mrrockka.service;

import by.mrrockka.creator.PrizePoolCreator;
import by.mrrockka.mapper.PrizePoolMapper;
import by.mrrockka.repo.prizepool.PrizePoolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrizePoolServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();

  @Mock
  private PrizePoolRepository prizePoolRepository;
  @Mock
  private PrizePoolMapper prizePoolMapper;
  @InjectMocks
  private PrizePoolService prizePoolService;

  @Test
  void givenGameId_whenGetPrizePoolExecuted_shouldReturnPrizePool() {
    final var entity = PrizePoolCreator.entity();
    final var expected = PrizePoolCreator.domain();

    when(prizePoolMapper.toDomain(entity))
      .thenReturn(expected);
    when(prizePoolRepository.findByGameId(GAME_ID))
      .thenReturn(Optional.of(entity));

    final var actual = prizePoolService.getByGameId(GAME_ID);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void givenGameIdAndPrizePool_whenStoreExecuted_shouldMapAndCallRepo() {
    final var entity = PrizePoolCreator.entity();
    final var domain = PrizePoolCreator.domain();

    when(prizePoolMapper.toEntity(GAME_ID, domain))
      .thenReturn(entity);
    prizePoolService.store(GAME_ID, domain);
    verify(prizePoolRepository).save(entity);
  }

}