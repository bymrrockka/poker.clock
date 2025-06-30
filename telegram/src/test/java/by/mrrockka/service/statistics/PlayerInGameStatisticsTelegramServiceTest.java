package by.mrrockka.service.statistics;

import by.mrrockka.creator.GameCreator;
import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.PersonCreator;
import by.mrrockka.domain.TelegramGame;
import by.mrrockka.domain.collection.PersonEntries;
import by.mrrockka.domain.statistics.PlayerInGameStatistics;
import by.mrrockka.domain.statistics.StatisticsCommand;
import by.mrrockka.domain.statistics.StatisticsType;
import by.mrrockka.response.builder.PlayerInGameStatisticsResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.mentions.PlayerHasNoNicknameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerInGameStatisticsTelegramServiceTest {

  @Mock
  private PlayerInGameStatisticsResponseBuilder playerInGameStatisticsResponseBuilder;
  @Mock
  private GameTelegramFacadeService gameTelegramFacadeService;
  @Mock
  private PlayerInGameStatisticsService playerInGameStatisticsService;
  @InjectMocks
  private PlayerInGameStatisticsTelegramService playerInGameStatisticsTelegramService;

  @Test
  void givenStatisticsCommand_whenRetrieveStatisticsInvoked_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var telegramGame =
      TelegramGame.builder()
        .game(GameCreator.tournament())
        .messageMetadata(MessageMetadataCreator.domain())
        .build();

    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.PLAYER_IN_GAME)
      .build();

    final var playerInGameStatistics = PlayerInGameStatistics.builder()
      .moneyInGame(BigDecimal.ZERO)
      .personEntries(PersonEntries.builder()
                       .person(PersonCreator.domainRandom())
                       .entries(Collections.emptyList())
                       .build())
      .build();

    final var expectedMessage = "statistics";

    when(gameTelegramFacadeService.getGameByMessageMetadata(metadata)).thenReturn(Optional.of(telegramGame));
    when(playerInGameStatisticsService.retrieveStatistics(telegramGame.game(), metadata.getFromNickname())).thenReturn(
      playerInGameStatistics);
    when(playerInGameStatisticsResponseBuilder.response(playerInGameStatistics)).thenReturn(expectedMessage);

    final var expected = SendMessage.builder()
      .chatId(metadata.getChatId())
      .text(expectedMessage)
      .replyToMessageId(telegramGame.messageMetadata().getId())
      .build();

    assertThat(playerInGameStatisticsTelegramService.retrieveStatistics(statsCommand)).isEqualTo(expected);
  }

  @Test
  void givenMessageMetadata_whenNoAssociatedGameWithChatId_thenShouldTjrowException() {
    final var metadata = MessageMetadataCreator.domain();
    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.PLAYER_IN_GAME)
      .build();

    when(gameTelegramFacadeService.getGameByMessageMetadata(metadata)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> playerInGameStatisticsTelegramService.retrieveStatistics(statsCommand))
      .isInstanceOf(ChatGameNotFoundException.class);
  }

  @Test
  void givenMessageMetadata_whenMessageHasNoFromUsername_thenShouldTjrowException() {
    final var metadata = MessageMetadataCreator.domain(builder -> builder.fromNickname(null));
    final var telegramGame =
      TelegramGame.builder()
        .game(GameCreator.tournament())
        .messageMetadata(MessageMetadataCreator.domain())
        .build();

    final var statsCommand = StatisticsCommand.builder()
      .metadata(metadata)
      .type(StatisticsType.PLAYER_IN_GAME)
      .build();

    when(gameTelegramFacadeService.getGameByMessageMetadata(metadata)).thenReturn(Optional.of(telegramGame));

    assertThatThrownBy(() -> playerInGameStatisticsTelegramService.retrieveStatistics(statsCommand))
      .isInstanceOf(PlayerHasNoNicknameException.class);
  }

}