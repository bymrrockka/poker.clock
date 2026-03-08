package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.service.BountySummary
import by.mrrockka.service.BountyTournamentPlayerSummary
import by.mrrockka.service.CashPlayerSummary
import by.mrrockka.service.PlayerSummary
import by.mrrockka.service.TournamentPlayerSummary
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface PlayerSummaryRepo {
    fun store(gameId: UUID, playerSummaries: List<PlayerSummary>)
    fun findForPersonGames(gameIds: List<UUID>, personId: UUID): List<PlayerSummary>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PrizeSummaryRepoImpl : PlayerSummaryRepo {

    override fun store(gameId: UUID, playerSummaries: List<PlayerSummary>) {
        GameSummaryTable.batchUpsert(keys = arrayOf(GameSummaryTable.gameId, GameSummaryTable.personId), data = playerSummaries) {
            this[GameSummaryTable.gameId] = gameId
            this[GameSummaryTable.personId] = it.person.id
            this[GameSummaryTable.buyIn] = it.buyIn

            when (it) {
                is TournamentPlayerSummary -> {
                    this[GameSummaryTable.position] = it.position
                    this[GameSummaryTable.type] = SummaryType.TOURNAMENT.name
                    this[GameSummaryTable.prize] = it.prize
                    this[GameSummaryTable.entriesNum] = it.entriesNum
                }

                is BountyTournamentPlayerSummary -> {
                    this[GameSummaryTable.position] = it.position
                    this[GameSummaryTable.type] = SummaryType.BOUNTY.name
                    this[GameSummaryTable.prize] = it.prize
                    this[GameSummaryTable.bounty] = it.bounty.amount
                    this[GameSummaryTable.takenNum] = it.bounty.takenNum
                    this[GameSummaryTable.givenNum] = it.bounty.givenNum
                    this[GameSummaryTable.entriesNum] = it.entriesNum
                }

                is CashPlayerSummary -> {
                    this[GameSummaryTable.type] = SummaryType.CASH.name
                    this[GameSummaryTable.withdrawals] = it.withdrawals
                    this[GameSummaryTable.entriesNum] = 0
                }
            }
        }
    }

    override fun findForPersonGames(gameIds: List<UUID>, personId: UUID): List<PlayerSummary> {
        return GameSummaryTable
                .leftJoin(PersonTable)
                .selectAll()
                .where { (GameSummaryTable.gameId inList gameIds) and (PersonTable.id eq personId) }
                .map { it.toSummary() }
    }

    private fun ResultRow.toSummary(): PlayerSummary {
        return when (SummaryType.valueOf(this[GameSummaryTable.type])) {
            SummaryType.TOURNAMENT -> TournamentPlayerSummary(
                    person = this.toPerson(),
                    position = this[GameSummaryTable.position],
                    buyIn = this[GameSummaryTable.buyIn],
                    entriesNum = this[GameSummaryTable.entriesNum],
                    prize = this[GameSummaryTable.prize]!!,
            )

            SummaryType.BOUNTY -> BountyTournamentPlayerSummary(
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

            SummaryType.CASH -> CashPlayerSummary(
                    person = this.toPerson(),
                    buyIn = this[GameSummaryTable.buyIn],
                    withdrawals = this[GameSummaryTable.withdrawals]!!,
            )
        }
    }

    private fun ResultRow.toPerson(): BasicPerson {
        return BasicPerson(
                id = this[PersonTable.id],
                nickname = this[PersonTable.nickName],
        )
    }

    private enum class SummaryType {
        CASH, BOUNTY, TOURNAMENT
    }
}
