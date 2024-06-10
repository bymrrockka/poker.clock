package by.mrrockka.bot.command.processor;

import by.mrrockka.creator.MessageMetadataCreator;
import by.mrrockka.creator.SendMessageCreator;
import by.mrrockka.service.WithdrawalTelegramService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawalTelegramCommandProcessorTest {
  @Mock
  private WithdrawalTelegramService withdrawalTelegramService;
  @InjectMocks
  private WithdrawalTelegramCommandProcessor withdrawalTelegramCommandProcessor;

  @Test
  void givenMessageMetadata_whenProcessorExecuted_thenShouldReturnBotMessage() {
    final var metadata = MessageMetadataCreator.domain();
    final var expected = SendMessageCreator.api();

    when(withdrawalTelegramService.storeWithdrawal(metadata)).thenReturn(expected);
    assertThat(withdrawalTelegramCommandProcessor.process(metadata)).isEqualTo(expected);
  }
}