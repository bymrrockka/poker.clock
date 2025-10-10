package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.Person
import by.mrrockka.domain.Seat
import by.mrrockka.repo.GameSeatsRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random
import kotlin.collections.ArrayDeque

interface GameSeatsService {
    fun generate(game: Game): Set<Seat>
    fun seed(seed: Long)
    fun entries(game: Game, persons: List<Person>): Set<Seat>
}

@Service
@Transactional
open class GameSeatsServiceImpl(
        private val gameSeatsRepo: GameSeatsRepo,
) : GameSeatsService {
    private var random: Random = Random()

    override fun generate(game: Game): Set<Seat> {
        val randomSeats = ArrayDeque((1..10).shuffled(random))

        val seats = game.players.map { it.person }.map { person ->
            Seat(num = randomSeats.removeFirst(), nickname = person.nickname ?: error("Person should have nickname"))
        }.toSet()

        gameSeatsRepo.store(game, seats)

        return seats
    }

    override fun seed(seed: Long) {
        random.setSeed(seed)
    }

    override fun entries(game: Game, persons: List<Person>): Set<Seat> {
        val seated = gameSeatsRepo.selectBy(game)
        val nicknameToSeat = seated.associateBy { it.nickname }
        val seatedPositions = seated.map { it.num }
        val empty = ArrayDeque((1..10).filterNot { seatedPositions.contains(it) }.shuffled(random))

        val seats = persons.map { person ->
            nicknameToSeat[person.nickname] ?: Seat(
                    num = empty.removeFirst(),
                    nickname = person.nickname ?: error("Person should have nickname"),
            )
        }.toSet()

        gameSeatsRepo.store(game, seated + seats)

        return seats
    }

}