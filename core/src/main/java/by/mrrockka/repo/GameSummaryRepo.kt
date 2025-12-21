package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.CashSummary
import by.mrrockka.domain.GameSummary
import by.mrrockka.domain.TournamentSummary
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface GameSummaryRepo {
    fun store(gameId: UUID, prizeSummaries: List<GameSummary>)
    fun findForPersonGames(gameIds: List<UUID>, personId: UUID): List<GameSummary>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PrizeSummaryRepoImpl : GameSummaryRepo {

    override fun store(gameId: UUID, prizeSummaries: List<GameSummary>) {
        GameSummaryTable.batchUpsert(keys = arrayOf(GameSummaryTable.gameId, GameSummaryTable.personId), data = prizeSummaries) {
            this[GameSummaryTable.gameId] = gameId
            this[GameSummaryTable.personId] = it.person.id
            this[GameSummaryTable.amount] = it.amount

            when (it) {
                is TournamentSummary -> {
                    this[GameSummaryTable.position] = it.position
                    this[GameSummaryTable.type] = SummaryType.TOURNAMENT.name
                }

                is CashSummary -> {
                    this[GameSummaryTable.type] = SummaryType.CASH.name
                }
            }
        }
    }

    override fun findForPersonGames(gameIds: List<UUID>, personId: UUID): List<GameSummary> {
        return GameSummaryTable
                .leftJoin(PersonTable)
                .selectAll()
                .where { (GameSummaryTable.gameId inList gameIds) and (PersonTable.id eq personId) }
                .map { it.toSummary() }
    }

    private fun ResultRow.toSummary(): GameSummary {
        return when (SummaryType.valueOf(this[GameSummaryTable.type])) {
            SummaryType.TOURNAMENT -> TournamentSummary(
                    person = this.toPerson(),
                    position = this[GameSummaryTable.position]!!,
                    amount = this[GameSummaryTable.amount],
            )

            SummaryType.CASH -> CashSummary(
                    person = this.toPerson(),
                    amount = this[GameSummaryTable.amount],
            )
        }

    }

    private fun ResultRow.toPerson(): BasicPerson {
        return BasicPerson(
                id = this[PersonTable.id],
                firstname = this[PersonTable.firstName],
                lastname = this[PersonTable.lastName],
                nickname = this[PersonTable.nickName],
        )
    }

    private enum class SummaryType {
        CASH, TOURNAMENT
    }
}
