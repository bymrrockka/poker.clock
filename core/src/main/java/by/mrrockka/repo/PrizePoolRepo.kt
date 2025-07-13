package by.mrrockka.repo

import by.mrrockka.domain.PositionPrize
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class PrizePoolRepo {
    fun findById(gameId: UUID): List<PositionPrize> {
        return PrizePoolTable.selectAll()
                .where { PrizePoolTable.gameId eq gameId }
                .first()
                .let { it[PrizePoolTable.schema].toList() }
    }

}
