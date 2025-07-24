package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.game.CashGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.validation.mentions.PersonMentionsValidator
import by.mrrockka.validation.withdrawals.WithdrawalsValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

//todo: refactor
@Service
class WithdrawalTelegramService(
        val withdrawalsService: WithdrawalsService,
        val withdrawalMessageParser: WithdrawalMessageParser,
        val gameTelegramFacadeService: GameTelegramService,
        val telegramPersonServiceOld: TelegramPersonServiceOld,
        val personMentionsValidator: PersonMentionsValidator,
        val withdrawalsValidator: WithdrawalsValidator,
) {
    fun storeWithdrawal(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        personMentionsValidator.validateMessageMentions(messageMetadata, 1)

        val (amount, nicknames) = withdrawalMessageParser.parse(messageMetadata)
        val telegramGame = gameTelegramFacadeService.findGame(messageMetadata)
        check(telegramGame.game is CashGame) { "Withdrawals are not allowed for non cash game" }

//        withdrawalsValidator.validateWithdrawalsAgainstEntries(
//                personAndAmountMap,
//                telegramGame.game.asType<CashGame?>(CashGame::class.java)
//        )

        val persons = telegramPersonServiceOld.getAllByNicknamesAndChatId(
                nicknames.toList(),
                messageMetadata.chatId
        )

        withdrawalsService.storeBatch(
                telegramGame.game.id,
                persons.map { it.id },
                amount,
                messageMetadata.createdAt
        )

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("""
                    Withdrawals: 
                    ${nicknames.joinToString { "    - @${it} -> $amount" }}
                """.trimIndent())
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}
