package by.mrrockka.service;

import by.mrrockka.domain.MessageMetadata;
import by.mrrockka.response.builder.CalculationResponseBuilder;
import by.mrrockka.service.calculation.CalculationService;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.PayoutsAreNotCalculatedException;
import by.mrrockka.service.game.GameTelegramFacadeService;
import by.mrrockka.validation.calculation.CalculationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationTelegramService {

  private final CalculationService calculationService;
  private final GameTelegramFacadeService gameTelegramFacadeService;
  private final CalculationResponseBuilder calculationResponseBuilder;
  private final CalculationValidator calculationValidator;

  public BotApiMethodMessage calculatePayouts(final MessageMetadata messageMetadata) {

    final var telegramGame = gameTelegramFacadeService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);

    calculationValidator.validateGame(telegramGame.game());

    final var payouts = calculationService.calculateAndSave(telegramGame.game());
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
