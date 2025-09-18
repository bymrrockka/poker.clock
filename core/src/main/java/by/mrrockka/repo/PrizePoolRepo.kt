package by.mrrockka.repo

import by.mrrockka.domain.PositionPrize
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface PrizePoolRepo {
    fun findById(gameId: UUID): List<PositionPrize>
    fun store(gameId: UUID, prizePool: List<PositionPrize>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PrizePoolRepoImpl : PrizePoolRepo {
    override fun findById(gameId: UUID): List<PositionPrize> {
        return PrizePoolTable.selectAll()
                .where { PrizePoolTable.gameId eq gameId }
                .firstOrNull()
                ?.let { it[PrizePoolTable.schema].toList() } ?: emptyList()
    }

    override fun store(gameId: UUID, prizePool: List<PositionPrize>) {
        PrizePoolTable.insert {
            it[PrizePoolTable.gameId] = gameId
            it[PrizePoolTable.schema] = prizePool.toTypedArray()
        }
    }

}
