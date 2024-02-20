package by.mrrockka.service;

import by.mrrockka.creator.PlayerCreator;
import by.mrrockka.mapper.PlayerMapper;
import by.mrrockka.repo.entries.EntriesEntity;
import by.mrrockka.repo.entries.EntriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

  private static final UUID GAME_ID = UUID.randomUUID();

  @Mock
  private EntriesRepository entriesRepository;
  @Mock
  private PlayerMapper playerMapper;
  @InjectMocks
  private PlayerService playerService;

  @Test
  void givenGameId_whenGetAllForGameExecuted_shouldReturnPlayers() {
    final var entries = entries();
    final var player = PlayerCreator.player();

    when(entriesRepository.findAllByGameId(GAME_ID))
      .thenReturn(List.of(entries));
    when(playerMapper.toPlayer(entries))
      .thenReturn(player);

    final var actual = playerService.getAllForGame(GAME_ID);

    assertThat(actual).isEqualTo(List.of(player));
  }

  private EntriesEntity entries() {
    return EntriesEntity.builder().build();
  }

}