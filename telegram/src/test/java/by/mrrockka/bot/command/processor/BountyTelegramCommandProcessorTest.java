package by.mrrockka.bot.command.processor;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.SendMessageCreator;
import by.mrrockka.service.BountyTelegramService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BountyTelegramCommandProcessorTest {

  @Mock
  private BountyTelegramService bountyTelegramService;
  @InjectMocks
  private BountyTelegramCommandProcessor bountyTelegramCommandProcessor;

  @Test
  void givenMessageMetadata_whenProcessorExecuted_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var expected = SendMessageCreator.model();

    when(bountyTelegramService.storeBounty(metadata)).thenReturn(expected);
    assertThat(bountyTelegramCommandProcessor.process(metadata)).isEqualTo(expected);
  }
}