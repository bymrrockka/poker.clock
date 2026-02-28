package by.mrrockka.repo

import by.mrrockka.domain.Game
import by.mrrockka.domain.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface GameTablesRepo {
    fun store(game: Game, tables: List<Table>)
    fun selectBy(game: Game): List<Table>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class GameSeatsRepoImpl : GameTablesRepo {

    override fun store(game: Game, tables: List<Table>) {
        GameTablesTable.upsert(GameTablesTable.gameId) {
            it[this.gameId] = game.id
            it[this.tables] = tables.toTypedArray()
        }
    }

    override fun selectBy(game: Game): List<Table> {
        return GameTablesTable.select(GameTablesTable.tables)
                .where(GameTablesTable.gameId eq game.id)
                .firstOrNull()
                .let { it?.get(GameTablesTable.tables)?.toList() ?: emptyList() }
    }

}