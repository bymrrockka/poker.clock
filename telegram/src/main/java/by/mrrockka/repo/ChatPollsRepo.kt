package by.mrrockka.repo

import org.jetbrains.exposed.v1.jdbc.insert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ChatPollsRepo {
    fun store(pollId: UUID, tgPollId: String)
}

@Repository
@Transactional
open class ChatPollsRepoImpl : ChatPollsRepo {

    override fun store(pollId: UUID, tgPollId: String) {
        ChatPollsTable.insert {
            it[ChatPollsTable.pollId] = pollId
            it[ChatPollsTable.tgPollId] = tgPollId
        }
    }
}