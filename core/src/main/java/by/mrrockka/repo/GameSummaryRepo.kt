package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.BountySummary
import by.mrrockka.domain.BountyTournamentSummary
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
            this[GameSummaryTable.buyIn] = it.buyIn

            when (it) {
                is TournamentSummary -> {
                    this[GameSummaryTable.position] = it.position
                    this[GameSummaryTable.type] = SummaryType.TOURNAMENT.name
                    this[GameSummaryTable.prize] = it.prize
                    this[GameSummaryTable.entriesNum] = it.entriesNum
                }

                is BountyTournamentSummary -> {
                    this[GameSummaryTable.position] = it.position
                    this[GameSummaryTable.type] = SummaryType.BOUNTY.name
                    this[GameSummaryTable.prize] = it.prize
                    this[GameSummaryTable.bounty] = it.bounty.amount
                    this[GameSummaryTable.takenNum] = it.bounty.takenNum
                    this[GameSummaryTable.givenNum] = it.bounty.givenNum
                    this[GameSummaryTable.entriesNum] = it.entriesNum
                }

                is CashSummary -> {
                    this[GameSummaryTable.type] = SummaryType.CASH.name
                    this[GameSummaryTable.withdrawals] = it.withdrawals
                    this[GameSummaryTable.entriesNum] = 0
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
                    position = this[GameSummaryTable.position],
                    buyIn = this[GameSummaryTable.buyIn],
                    entriesNum = this[GameSummaryTable.entriesNum],
                    prize = this[GameSummaryTable.prize]!!,
            )

            SummaryType.BOUNTY -> BountyTournamentSummary(
                    person = this.toPerson(),
                    buyIn = this[GameSummaryTable.buyIn],
                    entriesNum = this[GameSummaryTable.entriesNum],
                    prize = this[GameSummaryTable.prize]!!,
                    position = this[GameSummaryTable.position],
                    bounty = BountySummary(
                            amount = this[GameSummaryTable.bounty]!!,
                            takenNum = this[GameSummaryTable.takenNum]!!,
                            givenNum = this[GameSummaryTable.givenNum]!!,
                    ),
            )

            SummaryType.CASH -> CashSummary(
                    person = this.toPerson(),
                    buyIn = this[GameSummaryTable.buyIn],
                    withdrawals = this[GameSummaryTable.withdrawals]!!,
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
        CASH, BOUNTY, TOURNAMENT
    }
}
