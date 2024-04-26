package by.mrrockka.service;

import by.mrrockka.features.calculation.CalculationService;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.response.builder.CalculationResponseBuilder;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.PayoutsAreNotCalculatedException;
import by.mrrockka.service.game.TelegramGameService;
import by.mrrockka.validation.calculation.CalculationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramDetailsService {

  private final CalculationService calculationService;
  private final TelegramGameService telegramGameService;
  private final MessageMetadataMapper messageMetadataMapper;
  private final CalculationResponseBuilder calculationResponseBuilder;
  private final CalculationValidator calculationValidator;

  public BotApiMethodMessage calculatePayments(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());

    log.debug("Processing {\n%s\n} message from %s chat id.".
                formatted(messageMetadata.command(), messageMetadata.chatId()));

    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    calculationValidator.validateGame(telegramGame.game());

    final var payouts = calculationService.calculate(telegramGame.game());
    if (payouts.isEmpty()) {
      throw new PayoutsAreNotCalculatedException();
    }

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text(calculationResponseBuilder.response(payouts, telegramGame.game()))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }

}
