package by.mrrockka.mapper;

import by.mrrockka.model.Game;
import by.mrrockka.model.GameType;
import by.mrrockka.model.Person;
import lombok.Builder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GameMapperTest {

  private final GameMapper gameMapper = new GameMapper();

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
            .persons(List.of(
              person("@mrrockka"),
              person("@ivan"),
              person("@andrei"),
              person("@me")
            ))
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
            .persons(List.of(
              person("@mrrockka"),
              person("@me")
            ))
            .build())
          .build()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("tournamentMessages")
  void givenTournamentMessage_whenMapExecuted_thenShouldCreateGame(GameArgument argument) {
    assertThat(gameMapper.map(argument.message())).isEqualTo(argument.game());
  }

  private static Person person(String telegram) {
    return Person.builder()
      .telegram(telegram)
      .build();
  }
}