package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.moneyInGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.repo.WithdrawalsRepo
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class WithdrawalTelegramService(
        val withdrawalsRepo: WithdrawalsRepo,
        val withdrawalMessageParser: WithdrawalMessageParser,
        val gameTelegramService: GameTelegramService,
        val telegramPersonService: TelegramPersonService,
) {

    fun withdraw(messageMetadata: MessageMetadata): Pair<BigDecimal, Set<String>> {
        messageMetadata.checkMentions()

        val (amount, nicknames) = withdrawalMessageParser.parse(messageMetadata)
        val telegramGame = gameTelegramService.findGame(messageMetadata)
        check(telegramGame.game is CashGame) { "Withdrawals are not allowed for non cash game" }
        check(amount * nicknames.size.toBigDecimal() <= telegramGame.game.moneyInGame()) { "Sum of withdrawals is bigger then ${telegramGame.game.moneyInGame()} active in game" }

        val personsIds = telegramPersonService.findByMessage(messageMetadata)
        withdrawalsRepo.storeBatch(telegramGame.game.id, personsIds, amount, messageMetadata.createdAt)

        return amount to nicknames
    }
}
