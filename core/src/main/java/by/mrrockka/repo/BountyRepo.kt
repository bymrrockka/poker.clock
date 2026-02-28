package by.mrrockka.repo

import by.mrrockka.domain.Bounty
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

interface BountyRepo {
    fun findByPerson(gameId: UUID, personId: UUID): List<Bounty>
    fun store(gameId: UUID, bounty: Bounty, createdAt: Instant)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class BountyRepoImpl(
        private val personRepo: PersonRepo,
) : BountyRepo {
    override fun findByPerson(gameId: UUID, personId: UUID): List<Bounty> {
        return BountyTable.selectAll()
                .where {
                    (BountyTable.gameId eq gameId) and
                            ((BountyTable.to_person eq personId) or (BountyTable.from_person eq personId))
                }.map { it.toBounty() }
    }

    override fun store(gameId: UUID, bounty: Bounty, createdAt: Instant) {
        BountyTable.insert {
            it[BountyTable.gameId] = gameId
            it[BountyTable.from_person] = bounty.from.id
            it[BountyTable.to_person] = bounty.to.id
            it[BountyTable.createdAt] = createdAt
            it[BountyTable.amount] = bounty.amount
        }
    }

    private fun ResultRow.toBounty(): Bounty {
        val fromId = this[BountyTable.from_person]
        val toId = this[BountyTable.to_person]
        val persons = personRepo.findByIds(setOf(fromId, toId))
                .associateBy { it.id }
        check(persons.size == 2) { "Persons are not found" }
        return Bounty(
                from = persons[fromId]!!,
                to = persons[toId]!!,
                amount = this[BountyTable.amount],
        )
    }
}