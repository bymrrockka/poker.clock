package by.mrrockka.service;

import by.mrrockka.domain.game.CashGame;
import by.mrrockka.domain.game.GameType;
import by.mrrockka.mapper.MessageMetadataMapper;
import by.mrrockka.mapper.WithdrawalMessageMapper;
import by.mrrockka.service.exception.ChatGameNotFoundException;
import by.mrrockka.service.exception.ProcessingRestrictedException;
import by.mrrockka.service.game.TelegramGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class TelegramWithdrawalService {

  private final WithdrawalsService withdrawalsService;
  private final WithdrawalMessageMapper withdrawalMessageMapper;
  private final TelegramGameService telegramGameService;
  private final TelegramPersonService telegramPersonService;
  private final MessageMetadataMapper messageMetadataMapper;

  public BotApiMethodMessage storeWithdrawal(final Update update) {
    final var messageMetadata = messageMetadataMapper.map(update.getMessage());
    final var nicknameAndAmount = withdrawalMessageMapper.map(messageMetadata.command());
    final var telegramGame = telegramGameService
      .getGameByMessageMetadata(messageMetadata)
      .orElseThrow(ChatGameNotFoundException::new);


    if (!(telegramGame.game() instanceof CashGame)) {
      throw new ProcessingRestrictedException(GameType.CASH);
    }

    final var game = telegramGame.game();
    final var person = telegramPersonService.getByTelegramAndChatId(nicknameAndAmount.getKey(),
                                                                    messageMetadata.chatId());

    withdrawalsService.storeWithdrawal(game.getId(), person.getId(), nicknameAndAmount.getValue(),
                                       messageMetadata.createdAt());

    return SendMessage.builder()
      .chatId(messageMetadata.chatId())
      .text("%s withdrawn %s amount.".formatted(nicknameAndAmount.getKey(), nicknameAndAmount.getValue()))
      .replyToMessageId(telegramGame.messageMetadata().id())
      .build();
  }
}
