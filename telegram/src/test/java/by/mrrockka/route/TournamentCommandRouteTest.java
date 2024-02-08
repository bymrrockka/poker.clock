package by.mrrockka.route;

import by.mrrockka.mapper.game.GameMessageMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static by.mrrockka.route.creator.SendCreator.sendMessage;
import static by.mrrockka.route.creator.UpdateCreator.message;
import static by.mrrockka.route.creator.UpdateCreator.update;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TournamentCommandRouteTest {

  @Mock
  private GameMessageMapper gameMessageMapper;
  @InjectMocks
  private TournamentCommandRoute tournamentCommandRoute;

  private static Stream<Arguments> tournamentMessages() {
    return Stream.of(
      Arguments.of(
        """
                    /tournament 
                    buy-in: 30  
                    stack: 30k 
                    players: 
                      @mrrockka
                    @ivan 
                     @andrei 
                    me   
          """
      ),
      Arguments.of(
        """
                    /tournament 
                    buyin:      100  
                    stack:30000 
                    players: 
                      @mrrockka
                    me   
          """
      )
    );
  }

  @ParameterizedTest
  @MethodSource("tournamentMessages")
  void givenTournamentStartMessage_whenReceived_thenShouldValidateMessageAndStoreDataAndReturnGameId(String text) {
    final var update = update(message(text));
    final var expected = sendMessage(builder ->
      builder
        .chatId(update.getMessage().getChatId())
        .text(""));
    tournamentCommandRoute.process(update);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "/tournament",
    "/tournament ",
    "/tournament asdasd",
    "/tournament 123123"
  })
  void givenTournamentCommand_whenReceived_thenShouldMarkAsApplicable(String text) {
    assertThat(tournamentCommandRoute.isApplicable(update(message(text)))).isTrue();
  }
}