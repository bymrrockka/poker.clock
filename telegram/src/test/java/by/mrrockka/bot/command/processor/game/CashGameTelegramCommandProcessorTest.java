package by.mrrockka.bot.command.processor.game;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.SendMessageCreator;
import by.mrrockka.service.GameTelegramService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashGameTelegramCommandProcessorTest {
  @Mock
  private GameTelegramService gameTelegramService;
  @InjectMocks
  private CashGameTelegramCommandProcessor cashGameTelegramCommandProcessor;

  @Test
  void givenMessageMetadata_whenProcessorExecuted_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var expected = SendMessageCreator.api();

    when(gameTelegramService.storeGame(metadata)).thenReturn(expected);
    assertThat(cashGameTelegramCommandProcessor.process(metadata)).isEqualTo(expected);
  }
}