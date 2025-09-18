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
        val game = gameTelegramService.findGame(messageMetadata)
        check(game is CashGame) { "Withdrawals are not allowed for non cash game" }
        check(amount * nicknames.size.toBigDecimal() <= game.moneyInGame()) { "Sum of withdrawals is bigger then ${game.moneyInGame()} active in game" }

        val personsIds = telegramPersonService.findByMessage(messageMetadata).map { it.id }
        withdrawalsRepo.store(game.id, personsIds, amount, messageMetadata.createdAt)

        return nicknames to amount
    }
}
