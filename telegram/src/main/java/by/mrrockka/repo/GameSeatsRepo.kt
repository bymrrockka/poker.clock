package by.mrrockka.repo

import by.mrrockka.domain.Game
import by.mrrockka.domain.Seat
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface GameSeatsRepo {
    fun store(game: Game, seats: Set<Seat>)
    fun selectBy(game: Game): Set<Seat>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class GameSeatsRepoImpl : GameSeatsRepo {

    override fun store(game: Game, seats: Set<Seat>) {
        GameSeatsTable.upsert(GameSeatsTable.gameId) {
            it[this.gameId] = game.id
            it[this.seats] = seats.toTypedArray()
        }
    }

    override fun selectBy(game: Game): Set<Seat> {
        return GameSeatsTable.select(GameSeatsTable.seats)
                .where(GameSeatsTable.gameId eq game.id)
                .firstOrNull()
                .let { it?.get(GameSeatsTable.seats)?.toSet() ?: emptySet() }
    }

}