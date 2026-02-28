package by.mrrockka.repo

import by.mrrockka.domain.Person
import eu.vendeli.tgbot.types.common.PollAnswer
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface PollAnswersRepo {
    fun store(pollAnswer: PollAnswer, person: Person)
    fun find(pollId: String): Map<Int, List<UUID>>
}

@Repository
@Transactional
open class PollAnswersRepoImpl : PollAnswersRepo {

    override fun store(pollAnswer: PollAnswer, person: Person) {
        PollAnswersTable.upsert(PollAnswersTable.pollId, PollAnswersTable.personId) {
            it[PollAnswersTable.pollId] = pollAnswer.pollId
            it[PollAnswersTable.personId] = person.id
            it[PollAnswersTable.answer] = pollAnswer.optionIds.first()
        }
    }

    override fun find(pollId: String): Map<Int, List<UUID>> {
        return PollAnswersTable.selectAll()
                .where(PollAnswersTable.pollId eq pollId)
                .map { it[PollAnswersTable.answer] to it[PollAnswersTable.personId] }
                .groupBy({ it.first }, { it.second })
    }
}