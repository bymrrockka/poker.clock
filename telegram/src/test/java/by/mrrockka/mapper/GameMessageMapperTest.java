package by.mrrockka.mapper;

import by.mrrockka.domain.game.Game;
import by.mrrockka.domain.game.GameType;
import by.mrrockka.domain.player.Person;
import by.mrrockka.mapper.game.GameMessageMapper;
import by.mrrockka.mapper.game.NoBuyInException;
import by.mrrockka.mapper.game.NoPlayersException;
import by.mrrockka.mapper.game.NoStackException;
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
                      @ivan 
                       @andrei 
                      @me   
            """)
          .game(Game.builder()
            .gameType(GameType.TOURNAMENT)
            .stack(BigDecimal.valueOf(30000))
            .buyIn(BigDecimal.valueOf(30))
            //todo:
//            .persons(List.of(
//              person("@mrrockka"),
//              person("@ivan"),
//              person("@andrei"),
//              person("@me")
//            ))
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
          .game(Game.builder()
            .gameType(GameType.TOURNAMENT)
            .stack(BigDecimal.valueOf(50000))
            .buyIn(BigDecimal.valueOf(100))
            //todo:
//            .persons(List.of(
//              person("@mrrockka"),
//              person("@me")
//            ))
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
                      @ivan 
                       @andrei 
                      @ivan 
                       @andrei 
                      @me   
            """)
          .game(Game.builder()
            .gameType(GameType.TOURNAMENT)
            .stack(BigDecimal.valueOf(1500))
            .buyIn(BigDecimal.valueOf(15))
            //todo:
//            .persons(List.of(
//              person("@mrrockka"),
//              person("@ivan"),
//              person("@andrei"),
//              person("@me")
//            ))
            .build())
          .build()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("tournamentMessages")
  void givenTournamentMessage_whenMapExecuted_thenShouldCreateGame(GameArgument argument) {
    assertThat(gameMessageMapper.map(argument.message()))
      .usingRecursiveComparison()
      .ignoringFields("id")
      .isEqualTo(argument.game());
  }

  private static Stream<Arguments> noPlayersMessages() {
    return Stream.of(
      Arguments.of(
        """
            /tournament 
            buyin:      100  
            stack:50000
          """
      ),
      Arguments.of(
        """
            /tournament 
            buyin:      100  
            stack:50000 
            players: 
              @mrrockka
          """
      )
    );
  }

  @ParameterizedTest
  @MethodSource("noPlayersMessages")
  void givenMessage_whenNoPlayers_thenThrowException(String message) {
    assertThatThrownBy(() -> gameMessageMapper.map(message))
      .isInstanceOf(NoPlayersException.class);
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
    assertThatThrownBy(() -> gameMessageMapper.map(message))
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
    assertThatThrownBy(() -> gameMessageMapper.map(message))
      .isInstanceOf(NoBuyInException.class);
  }

  private static Person person(String telegram) {
    return Person.builder()
      .telegram(telegram)
      .build();
  }
}
