package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.Seat
import by.mrrockka.repo.GameSeatsRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random
import kotlin.collections.ArrayDeque

interface GameSeatsService {
    fun generate(game: Game): List<Seat>
    fun seed(seed: Long)
}

@Service
@Transactional
open class GameSeatsServiceImpl(
        private val gameSeatsRepo: GameSeatsRepo,
) : GameSeatsService {
    private var random: Random = Random()


    override fun generate(game: Game): List<Seat> {
        val randomSeats = ArrayDeque((1..10).shuffled(random))

        val seats = game.players.map { it.person }.map { person ->
            Seat(num = randomSeats.removeFirst(), nickname = person.nickname ?: error("Person should have nickname"))
        }.sortedBy { it.num }

        gameSeatsRepo.store(game, seats)

        return seats
    }

    override fun seed(seed: Long) {
        random.setSeed(seed)
    }

}