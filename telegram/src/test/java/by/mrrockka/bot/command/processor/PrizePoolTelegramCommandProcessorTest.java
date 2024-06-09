package by.mrrockka.bot.command.processor;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.SendMessageCreator;
import by.mrrockka.service.PrizePoolTelegramService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrizePoolTelegramCommandProcessorTest {
  @Mock
  private PrizePoolTelegramService prizePoolTelegramService;
  @InjectMocks
  private PrizePoolTelegramCommandProcessor prizePoolTelegramCommandProcessor;

  @Test
  void givenMessageMetadata_whenProcessorExecuted_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var expected = SendMessageCreator.model();

    when(prizePoolTelegramService.storePrizePool(metadata)).thenReturn(expected);
    assertThat(prizePoolTelegramCommandProcessor.process(metadata)).isEqualTo(expected);
  }
}