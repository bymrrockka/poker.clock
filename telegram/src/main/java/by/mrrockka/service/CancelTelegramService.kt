package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.repo.BountyRepo
import by.mrrockka.repo.ChatMessagesRepo
import by.mrrockka.repo.CommandType
import by.mrrockka.repo.EntriesRepo
import by.mrrockka.repo.WithdrawalsRepo
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.springframework.stereotype.Service
import kotlin.time.Clock
import kotlin.time.toJavaInstant

interface CancelTelegramService {
    fun cancel(metadata: MessageMetadata): CommandType
}

@Service
open class CancelTelegramServiceImpl(
        private val entriesRepo: EntriesRepo,
        private val bountiesRepo: BountyRepo,
        private val withdrawalRepo: WithdrawalsRepo,
        private val chatMessagesRepo: ChatMessagesRepo,
        private val clock: Clock,
) : CancelTelegramService {
    override fun cancel(metadata: MessageMetadata): CommandType {
        val reply = metadata.replyTo
        check(reply != null) { "Reply message should be added to cancel command" }

        val command = reply.command.text.lowercase().removePrefix("/")
                .let { text ->
                    CommandType.entries.find { it.name.lowercase() == text }
                            ?: error("Can't cancel $text")
                }

        return transaction {
            val operationId = chatMessagesRepo.getOperationId(reply.chatId, reply.id)
            check(operationId != null) { "Didn't found message in database" }
            val updatedAt = clock.now().toJavaInstant()
            when (command) {
                CommandType.BOUNTY -> bountiesRepo.update(operationId, updatedAt, isDeleted = true)
                CommandType.ENTRY -> entriesRepo.update(operationId, updatedAt, isDeleted = true)
                CommandType.WITHDRAWAL -> withdrawalRepo.update(operationId, updatedAt, isDeleted = true)
            }

            command
        }

    }

}