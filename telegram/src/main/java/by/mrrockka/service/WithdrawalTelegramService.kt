package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.moneyInGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.repo.WithdrawalsRepo
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface WithdrawalTelegramService {
    fun withdraw(messageMetadata: MessageMetadata): Pair<Set<String>, BigDecimal>
}

@Service
class WithdrawalTelegramServiceImpl(
        private val withdrawalsRepo: WithdrawalsRepo,
        private val withdrawalMessageParser: WithdrawalMessageParser,
        private val gameTelegramService: GameTelegramService,
        private val telegramPersonService: TelegramPersonService,
) : WithdrawalTelegramService {

    override fun withdraw(messageMetadata: MessageMetadata): Pair<Set<String>, BigDecimal> {
        messageMetadata.checkMentions()
        val (nicknames, amount) = withdrawalMessageParser.parse(messageMetadata)
        val telegramGame = gameTelegramService.findGame(messageMetadata)
        check(telegramGame.game is CashGame) { "Withdrawals are not allowed for non cash game" }
        check(amount * nicknames.size.toBigDecimal() <= telegramGame.game.moneyInGame()) { "Sum of withdrawals is bigger then ${telegramGame.game.moneyInGame()} active in game" }

        val personsIds = telegramPersonService.findByMessage(messageMetadata).map { it.id }
        withdrawalsRepo.storeBatch(telegramGame.game.id, personsIds, amount, messageMetadata.createdAt)

        return nicknames to amount
    }
}
