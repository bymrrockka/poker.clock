package by.mrrockka.route;

import by.mrrockka.creator.MessageCreator;
import by.mrrockka.creator.SendCreator;
import by.mrrockka.creator.UpdateCreator;
import by.mrrockka.route.commands.TournamentGameTelegramCommand;
import by.mrrockka.service.game.TelegramGameService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentGameTelegramCommandTest {

  @Mock
  private TelegramGameService telegramGameService;
  @InjectMocks
  private TournamentGameTelegramCommand tournamentCommandRoute;

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
  void givenTournamentStartMessage_whenReceived_thenShouldValidateMessageAndStoreDataAndReturnGameId(
    final String text) {
    final var update = UpdateCreator.update(MessageCreator.message(text));
    final var expected = SendCreator.sendMessage(builder -> builder
      .chatId(update.getMessage().getChatId())
      .text(""));

    when(telegramGameService.storeTournamentGame(update)).thenReturn(expected);

    assertThat(tournamentCommandRoute.process(update)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "/tournament",
    "/tournament ",
    "/tournament asdasd",
    "/tournament 123123"
  })
  void givenTournamentCommand_whenReceived_thenShouldMarkAsApplicable(final String text) {
    final var update = UpdateCreator.update(MessageCreator.message(text));

    assertThat(tournamentCommandRoute.isApplicable(update)).isTrue();
  }
}