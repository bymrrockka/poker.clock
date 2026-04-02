package by.mrrockka.repo

import by.mrrockka.domain.Bounty
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

interface BountyRepo {
    fun findByGame(gameId: UUID): List<Bounty>
    fun store(gameId: UUID, bounty: Bounty, createdAt: Instant)
    fun update(gameId: UUID, bounty: Bounty, updatedAt: Instant, isDeleted: Boolean = false)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class BountyRepoImpl(
        private val personRepo: PersonRepo,
) : BountyRepo {
    override fun findByGame(gameId: UUID): List<Bounty> {
        return BountyTable.selectAll()
                .where { (BountyTable.gameId eq gameId) and (BountyTable.isDeleted neq true) }
                .map { it.toBounty() }
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

    override fun update(gameId: UUID, bounty: Bounty, updatedAt: Instant, isDeleted: Boolean) {
        BountyTable.update(
                limit = 1,
                where = {
                    (BountyTable.gameId eq gameId) and
                            (BountyTable.from_person eq bounty.from.id) and
                            (BountyTable.to_person eq bounty.to.id) and
                            (BountyTable.isDeleted neq true)
                },
        ) {
            it[BountyTable.isDeleted] = isDeleted
            it[BountyTable.updatedAt] = updatedAt
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