package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.game.CashGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.service.exception.ChatGameNotFoundException
import by.mrrockka.service.game.GameTelegramFacadeService
import by.mrrockka.validation.GameValidator
import by.mrrockka.validation.collection.CollectionsValidator
import by.mrrockka.validation.mentions.PersonMentionsValidator
import by.mrrockka.validation.withdrawals.WithdrawalsValidator
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.function.Supplier

@Service
class WithdrawalTelegramService(
        val withdrawalsService: WithdrawalsService,
        val withdrawalMessageParser: WithdrawalMessageParser,
        val gameTelegramFacadeService: GameTelegramFacadeService,
        val telegramPersonService: TelegramPersonService,
        val gameValidator: GameValidator,
        val personMentionsValidator: PersonMentionsValidator,
        val collectionsValidator: CollectionsValidator,
        val withdrawalsValidator: WithdrawalsValidator,
) {
    fun storeWithdrawal(messageMetadata: MessageMetadata): BotApiMethodMessage? {
        personMentionsValidator.validateMessageMentions(messageMetadata, 1)

        val personAndAmountMap = withdrawalMessageParser.parse(messageMetadata)
        collectionsValidator.validateMapIsNotEmpty(personAndAmountMap, "Withdrawal")

        val amount = personAndAmountMap.values.stream().findFirst().orElseThrow()
        val telegramGame = gameTelegramFacadeService
                .getGameByMessageMetadata(messageMetadata)
                .orElseThrow { ChatGameNotFoundException() }
        gameValidator.validateGameIsCashType(telegramGame.game)
        withdrawalsValidator.validateWithdrawalsAgainstEntries(
                personAndAmountMap,
                telegramGame.game.asType<CashGame?>(CashGame::class.java)
        )

        val persons = telegramPersonService.getAllByNicknamesAndChatId(
                personAndAmountMap.keys.map { it.nickname },
                messageMetadata.chatId
        )

        withdrawalsService.storeBatch(
                telegramGame.game.getId(),
                persons.map { it.id },
                amount,
                messageMetadata.createdAt
        )

        return SendMessage.builder()
                .chatId(messageMetadata.chatId)
                .text("""
                    Withdrawals: 
                    ${personAndAmountMap.entries.joinToString { (key, value) -> "    - @${key.nickname} -> $value" }}
                """.trimIndent())
                .replyToMessageId(telegramGame.messageMetadata.id)
                .build()
    }
}
