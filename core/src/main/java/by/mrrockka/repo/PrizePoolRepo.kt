package by.mrrockka.repo

import by.mrrockka.domain.PositionPrize
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface PrizePoolRepo {
    fun findById(gameId: UUID): List<PositionPrize>
}

@Repository
@Transactional
open class PrizePoolRepoImpl : PrizePoolRepo {
    override fun findById(gameId: UUID): List<PositionPrize> {
        return PrizePoolTable.selectAll()
                .where { PrizePoolTable.gameId eq gameId }
                .firstOrNull()
                ?.let { it[PrizePoolTable.schema].toList() } ?: emptyList()
    }

}
