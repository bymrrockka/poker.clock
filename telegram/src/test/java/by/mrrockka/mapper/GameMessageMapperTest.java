package by.mrrockka.mapper;

import by.mrrockka.domain.game.Game;
import by.mrrockka.mapper.game.GameFieldIsNotSpecifiedException;
import by.mrrockka.mapper.game.GameMessageMapper;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameMessageMapperTest {

  private final GameMessageMapper gameMessageMapper = new GameMessageMapper();

  @Builder
  private record GameArgument(String message, GameMeta game) {}

  @Builder
  private record GameMeta(BigDecimal buyIn, BigDecimal stack) {}

  private static Stream<Arguments> gameMessages() {
    return Stream.of(
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament 
                     buy-in: 30  
                     stack: 30k 
                     bounty:30
                     players: 
                       @mrrockka
                     @ivano 
                      @andrei 
                     @me   
                         """)
          .game(GameMeta.builder()
                  .stack(BigDecimal.valueOf(30000))
                  .buyIn(BigDecimal.valueOf(30))
                  .build())
          .build()
      ),
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament  
                     bounty:30
                     buyin:      100  
                     stack:50000 
                     players: 
                       @mrrockka
                     @me   
                           """)
          .game(GameMeta.builder()
                  .stack(BigDecimal.valueOf(50000))
                  .buyIn(BigDecimal.valueOf(100))
                  .build())
          .build()
      ),
      Arguments.of(
        GameArgument.builder()
          .message("""
                     /tournament 
                     buyin:    15   
                     bounty:30 
                     stack: 1.5k
                       @mrrockka
                     @ivano 
                      @andrei 
                     @ivano 
                      @andrei 
                     @me   
                                 """)
          .game(GameMeta.builder()
                  .stack(BigDecimal.valueOf(1500))
                  .buyIn(BigDecimal.valueOf(15))
                  .build())
          .build()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("gameMessages")
  void givenTournamentMessage_whenMapExecuted_thenShouldCreateGame(final GameArgument argument) {
    assertThat(gameMessageMapper.mapTournament(argument.message()))
      .usingRecursiveComparison()
      .ignoringExpectedNullFields()
      .isEqualTo(argument.game());
    assertThat((Game) gameMessageMapper.mapCash(argument.message()))
      .usingRecursiveComparison()
      .ignoringExpectedNullFields()
      .isEqualTo(argument.game());
    assertThat((Game) gameMessageMapper.mapBounty(argument.message()))
      .usingRecursiveComparison()
      .ignoringExpectedNullFields()
      .isEqualTo(argument.game());
  }

  @Test
  void givenMessage_whenNoStack_thenThrowException() {
    final var message =
      """
        /tournament
        buyin:    15
        players: 
          @mrrockka
          @me
        """;
    assertThatThrownBy(() -> gameMessageMapper.mapTournament(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
    assertThatThrownBy(() -> gameMessageMapper.mapCash(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
    assertThatThrownBy(() -> gameMessageMapper.mapBounty(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
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
    assertThatThrownBy(() -> gameMessageMapper.mapTournament(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
    assertThatThrownBy(() -> gameMessageMapper.mapCash(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
    assertThatThrownBy(() -> gameMessageMapper.mapBounty(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
  }

  @Test
  void givenMessage_whenNoBounty_thenThrowException() {
    final var message =
      """
        /tournament
        buyin:    15  
        stack: 1.5k  
        players: 
          @mrrockka
          @me
        """;
    assertThatThrownBy(() -> gameMessageMapper.mapBounty(message))
      .isInstanceOf(GameFieldIsNotSpecifiedException.class);
  }
}
