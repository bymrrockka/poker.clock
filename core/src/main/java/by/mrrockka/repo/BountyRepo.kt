package by.mrrockka.repo

import by.mrrockka.domain.Bounty
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
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
open class BountyRepoImpl : BountyRepo {
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
            it[BountyTable.from_person] = bounty.from
            it[BountyTable.to_person] = bounty.to
            it[BountyTable.createdAt] = createdAt
            it[BountyTable.amount] = bounty.amount
        }
    }

    private fun ResultRow.toBounty(): Bounty {
        return Bounty(
                from = this[BountyTable.from_person],
                to = this[BountyTable.to_person],
                amount = this[BountyTable.amount],
        )
    }
}