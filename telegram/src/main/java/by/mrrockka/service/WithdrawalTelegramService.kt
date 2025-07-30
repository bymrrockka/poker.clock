package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.moneyInGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.repo.WithdrawalsRepo
import by.mrrockka.validation.MentionsValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class WithdrawalTelegramService(
        val withdrawalsRepo: WithdrawalsRepo,
        val withdrawalMessageParser: WithdrawalMessageParser,
        val gameTelegramService: GameTelegramService,
        val telegramPersonService: TelegramPersonService,
        val mentionsValidator: MentionsValidator,
) {

    fun storeWithdrawal(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        mentionsValidator.validateMentions(messageMetadata)

        val (amount, nicknames) = withdrawalMessageParser.parse(messageMetadata)
        val telegramGame = gameTelegramService.findGame(messageMetadata)
        check(telegramGame.game is CashGame) { "Withdrawals are not allowed for non cash game" }
        check(amount * nicknames.size.toBigDecimal() <= telegramGame.game.moneyInGame()) { "Sum of withdrawals is bigger then ${telegramGame.game.moneyInGame()} active in game" }

        val personsIds = telegramPersonService.findByMessage(messageMetadata)
        withdrawalsRepo.storeBatch(telegramGame.game.id, personsIds, amount, messageMetadata.createdAt)

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text(
                        """
                        |Withdrawals: 
                        |${nicknames.joinToString { "|  - @${it} -> $amount" }}
                        """.trimMargin(),
                )
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}
