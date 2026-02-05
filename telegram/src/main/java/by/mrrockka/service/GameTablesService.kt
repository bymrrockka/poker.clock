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
        val (tables, _) = game.formTables(game.players.map { it.person })

        tablesRepo.store(game, tables)

        return tables
    }

    override fun seed(seed: Long) {
        random.setSeed(seed)
    }

    override fun entries(game: Game, persons: List<Person>): List<Table> {
        val (tables, updatedTables) = game.formTables(persons)

        tablesRepo.store(game, tables)
        return updatedTables
    }

    /**
     * Function creates and updates table seats for game
     * As far as table has only id in constructor when merging collections
     * @return fully combined and merged tables to only updated tables as pair
     * */
    private fun Game.formTables(entries: List<Person>): Pair<List<Table>, List<Table>> {
        val tables = tablesRepo.selectBy(this)
                .sortedBy { it.seats.size }
        val seated = tables.flatMap { it.byNicknames.keys }
        val newEntries = entries.map { it.nickname ?: error("Person should have nickname") }
                .filterNot { seated.contains(it) }

        return when {
            newEntries.isEmpty() -> tables to emptyList()

            tables.isEmpty() -> {
                val newTables = newEntries
                        .toTableApplicants(seatsAverage())
                        .generateTables()
                newTables to newTables
            }

            (tables.sumOf { it.emptySeats().size } - newEntries.size < 0) -> {
                //find players to move from existent tables
                val tablesToPopped = tables
                        .map { table -> table to table.seats.size - seatsAverage() }
                        .map { (table, popSize) ->
                            val toPop = (1..popSize)
                                    .map {
                                        var seat: Seat?
                                        var counter = 0
                                        do {
                                            seat = table.byNums[random.nextInt(maxSeats)]
                                        } while (seat == null && counter++ < maxSeats)
                                        seat ?: error("Table is empty")
                                    }
                            (table - toPop) to toPop
                        }.groupBy({ it.first }, { it.second })
                        .mapValues { (_, popped) -> popped.flatten() }
                        .mapValues { (_, seats) -> seats.map { it.nickname } }

                //sum new entries and players to move and create collection of table applicants
                //create new tables with table applicants
                val newTables = (newEntries + tablesToPopped.values.flatten())
                        .toTableApplicants(seatsAverage())
                        .generateTables(tables.size)

                //merge changes and return with updates
                (tablesToPopped.keys + newTables.toSet()).toList() to newTables
            }

            else -> {
                //find table for new entries
                val tableQueue = tables.toDeque()
                newEntries.forEach { entry ->
                    val table = tableQueue.removeFirst()
                    val emptySeats = table.emptySeats()
                    tableQueue.addLast(table + Seat(emptySeats.removeFirst(), entry))
                }
                //merge changes and return only updated seats with table
                tableQueue.toList() to tableQueue
                        .map { update -> Table(update.id, update.seats.filter { newEntries.contains(it.nickname) }.toSet()) }
                        .toList()
            }
        }
    }

    private fun List<List<String>>.generateTables(begin: Int = 0): List<Table> =
            (1..size)
                    .map { index -> index to (1..maxSeats).shuffled(random).toDeque() }
                    .map { (index, available) ->
                        Table(
                                id = begin + index,
                                seats = this[index - 1].map { nickname ->
                                    Seat(num = available.removeFirst(), nickname = nickname)
                                }.toSortedSet { o1, o2 -> o2.num - o1.num },
                        )
                    }


    private fun List<String>.toTableApplicants(tableSize: Int): List<List<String>> = shuffled(random).chunked(tableSize)

    private fun Table.emptySeats(): ArrayDeque<Int> {
        val seats = this.seats.map { it.num }
        return (1..maxSeats).filterNot { seats.contains(it) }.shuffled(random).toDeque()
    }

    private fun Game.seatsAverage(): Int =
            if (players.size > maxSeats) {
                val perTable = (players.size + maxSeats - 1) / maxSeats
                (players.size + perTable - 1) / perTable
            } else maxSeats

    private fun <T> Collection<T>.toDeque(): ArrayDeque<T> = ArrayDeque(this)
}