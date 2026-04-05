package by.mrrockka.service

import by.mrrockka.domain.CashGame
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.moneyInGame
import by.mrrockka.parser.WithdrawalMessageParser
import by.mrrockka.repo.ChatMessagesRepo
import by.mrrockka.repo.CommandType
import by.mrrockka.repo.WithdrawalsRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface WithdrawalTelegramService {
    fun withdraw(metadata: MessageMetadata): Pair<String, BigDecimal>
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class WithdrawalTelegramServiceImpl(
        private val withdrawalsRepo: WithdrawalsRepo,
        private val withdrawalMessageParser: WithdrawalMessageParser,
        private val gameTelegramService: GameTelegramService,
        private val telegramPersonService: TelegramPersonService,
        private val chatMessagesRepo: ChatMessagesRepo,
) : WithdrawalTelegramService {

    override fun withdraw(metadata: MessageMetadata): Pair<String, BigDecimal> {
        check(metadata.mentions.isEmpty()) { "Withdrawal command does not require mentions anymore. Use as /withdrawal #amount" }
        check(metadata.from?.username != null) { "User must have nickname to execute command" }
        val amount = withdrawalMessageParser.parse(metadata)
        val game = gameTelegramService.findGame(metadata)
        check(game is CashGame) { "Withdrawals are not allowed for non cash game" }
        check(amount <= game.moneyInGame()) { "Sum of withdrawals is bigger then ${game.moneyInGame()} active in game" }

        val person = telegramPersonService.findOrAdd(metadata.from.username!!, metadata.chatId)
        val operationId = withdrawalsRepo.store(game.id, person.id, amount, metadata.createdAt)
        chatMessagesRepo.store(metadata, operationId, CommandType.WITHDRAWAL)

        return person.nickname!! to amount
    }
}
