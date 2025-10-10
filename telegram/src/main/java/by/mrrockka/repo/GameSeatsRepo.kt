package by.mrrockka.repo

import by.mrrockka.domain.Game
import by.mrrockka.domain.Seat
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface GameSeatsRepo {
    fun store(game: Game, seats: List<Seat>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class GameSeatsRepoImpl : GameSeatsRepo {

    override fun store(game: Game, seats: List<Seat>) {
        GameSeatsTable.upsert(GameSeatsTable.gameId) {
            it[this.gameId] = game.id
            it[this.seats] = seats.toTypedArray()
        }
    }
}