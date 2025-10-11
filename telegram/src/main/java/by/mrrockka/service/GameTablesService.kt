package by.mrrockka.service

import by.mrrockka.domain.Game
import by.mrrockka.domain.Person
import by.mrrockka.domain.Seat
import by.mrrockka.domain.Table
import by.mrrockka.repo.GameTablesRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random
import kotlin.collections.ArrayDeque

interface GameTablesService {
    fun generate(game: Game): List<Table>
    fun entries(game: Game, persons: List<Person>): List<Table>
    fun seed(seed: Long)
}

@Service
@Transactional
open class GameTablesServiceImpl(
        private val tablesRepo: GameTablesRepo,
) : GameTablesService {
    private var random: Random = Random()
    private val maxSeats = 10

    override fun generate(game: Game): List<Table> {
        val tablesSize = if (game.players.size > maxSeats) {
            val perTable = (game.players.size + maxSeats - 1) / maxSeats
            (game.players.size + perTable - 1) / perTable
        } else maxSeats

        val playerTables = game.players.map {
            it.person.nickname ?: error("Person should have nickname")
        }.shuffled(random).chunked(tablesSize)

        val tables = (1..playerTables.size)
                .map { index -> index to (1..maxSeats).shuffled(random).toDeque() }
                .map { (index, available) ->
                    Table(
                            id = index,
                            seats = playerTables[index - 1].map { nickname ->
                                Seat(num = available.removeFirst(), nickname = nickname)
                            }.toSet(),
                    )
                }

        tablesRepo.store(game, tables)

        return tables
    }

    override fun seed(seed: Long) {
        random.setSeed(seed)
    }

    override fun entries(game: Game, persons: List<Person>): List<Table> {
        val stored = tablesRepo.selectBy(game)
                .sortedBy { it.seats.size }
        val nicknames = persons.map { it.nickname ?: error("Person should have nickname") }

        val tableAvailable = stored
                .map { it to it.seats.available() }
                .filterNot { (_, available) -> available.isEmpty() }
                .toDeque()
        val nicknameToTable = nicknames.associate { nickname ->
            nickname to (stored.find { it.byNicknames[nickname] != null })
        }

        val tables = nicknames.associate { nickname ->
            val (table, available) = tableAvailable.removeFirst()
            val withPerson = nicknameToTable[nickname]
                    ?: table.copy(seats = table.seats + Seat(available.removeFirst(), nickname))
            tableAvailable.add(withPerson to available)

            withPerson.id to withPerson
        }

        tablesRepo.store(game, (tables.values + stored.filterNot { tables.containsKey(it.id) }).sortedBy { it.id })
        return tables.values
                .map { table -> table.copy(seats = table.seats.filter { nicknames.contains(it.nickname) }.toSet()) }
                .filter { it.seats.isNotEmpty() }
    }

    private fun Set<Seat>.available(): ArrayDeque<Int> {
        val seats = this.map { it.num }
        return (1..maxSeats).filterNot { seats.contains(it) }.toDeque()
    }

    private fun <T> Collection<T>.toDeque(): ArrayDeque<T> = ArrayDeque(this)
}