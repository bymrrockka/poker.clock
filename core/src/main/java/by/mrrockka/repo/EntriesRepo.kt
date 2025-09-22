package by.mrrockka.repo

import by.mrrockka.domain.Game
import by.mrrockka.domain.Person
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.*

interface EntriesRepo {
    fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>>
    fun store(personIds: List<UUID>, amount: BigDecimal, game: Game, createdAt: Instant)
    fun findByPerson(person: Person): Map<UUID, List<BigDecimal>>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class EntriesRepoImpl : EntriesRepo {

    override fun findByGame(gameId: UUID): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { EntriesTable.gameId eq gameId }
                .map { it[EntriesTable.personId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }

    override fun store(personIds: List<UUID>, amount: BigDecimal, game: Game, createdAt: Instant) {
        EntriesTable.batchInsert(personIds) { id ->
            this[EntriesTable.gameId] = game.id
            this[EntriesTable.personId] = id
            this[EntriesTable.amount] = amount
            this[EntriesTable.createdAt] = createdAt
        }
    }

    override fun findByPerson(person: Person): Map<UUID, List<BigDecimal>> {
        return EntriesTable.selectAll()
                .where { EntriesTable.personId eq person.id }
                .map { it[EntriesTable.gameId] to it[EntriesTable.amount] }
                .groupBy({ it.first }, { it.second })
    }
}