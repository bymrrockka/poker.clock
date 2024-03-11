package by.mrrockka.mapper;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.TournamentGame;
import by.mrrockka.mapper.game.NoBuyInException;
import by.mrrockka.mapper.game.NoStackException;
import by.mrrockka.mapper.game.TournamentMessageMapper;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TournamentMessageMapperTest {

  private final TournamentMessageMapper tournamentMessageMapper = new TournamentMessageMapper();

  @Builder
  private record GameArgument(String message, Game game) {
  }

  private static Stream<Arguments> tournamentMessages() {
    return Stream.of(
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament 
                     buy-in: 30  
                     stack: 30k 
                     players: 
                       @mrrockka
                     @ivano 
                      @andrei 
                     @me   
                         """)
          .game(TournamentGame.tournamentBuilder()
                  .id(UUID.randomUUID())
                  .stack(BigDecimal.valueOf(30000))
                  .buyIn(BigDecimal.valueOf(30))
                  .build())
          .build()
      ),
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament 
                     buyin:      100  
                     stack:50000 
                     players: 
                       @mrrockka
                     @me   
                           """)
          .game(TournamentGame.tournamentBuilder()
                  .id(UUID.randomUUID())
                  .stack(BigDecimal.valueOf(50000))
                  .buyIn(BigDecimal.valueOf(100))
                  .build())
          .build()
      ),
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15zl    
                     stack: 1.5k
                       @mrrockka
                     @ivano 
                      @andrei 
                     @ivano 
                      @andrei 
                     @me   
                                 """)
          .game(TournamentGame.tournamentBuilder()
                  .id(UUID.randomUUID())
                  .stack(BigDecimal.valueOf(1500))
                  .buyIn(BigDecimal.valueOf(15))
                  .build())
          .build()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("tournamentMessages")
  void givenTournamentMessage_whenMapExecuted_thenShouldCreateGame(GameArgument argument) {
    assertThat(tournamentMessageMapper.mapTournament(argument.message()))
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(argument.game());
  }

  @Test
  void givenMessage_whenNoStack_thenThrowException() {
    final var message =
      """
        /tournament
        buyin:    15zl
        players: 
          @mrrockka
          @me
        """;
    assertThatThrownBy(() -> tournamentMessageMapper.mapTournament(message))
      .isInstanceOf(NoStackException.class);
  }

  @Test
  void givenMessage_whenNoBuyIn_thenThrowException() {
    final var message =
      """
        /tournament   
        stack: 1.5k 
        players: 
          @mrrockka
          @me
        """;
    assertThatThrownBy(() -> tournamentMessageMapper.mapTournament(message))
      .isInstanceOf(NoBuyInException.class);
  }
}
