package by.mrrockka.repo

import by.mrrockka.domain.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface GameRepo {
    fun save(game: Game)
    fun update(game: Game)
    fun findById(id: UUID): Game
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class GameRepoImpl(
        private val playerRepo: PlayerRepo,
        private val finalePlacesRepo: FinalePlacesRepo,
        private val prizePoolRepo: PrizePoolRepo,
) : GameRepo {

    override fun save(game: Game) {
        GameTable.insert {
            it[id] = game.id
            it[gameType] = game.toType()
            it[buyIn] = game.buyIn
            it[bounty] = game.let { if (it is BountyTournamentGame) it.bounty else null }
            it[stack] = game.stack
            it[createdAt] = game.createdAt
        }
    }

    override fun update(game: Game) {
        GameTable.insert {
            it[id] = game.id
            it[gameType] = game.toType()
            it[buyIn] = game.buyIn
            it[bounty] = game.let { if (it is BountyTournamentGame) it.bounty else null }
            it[stack] = game.stack
            it[createdAt] = game.createdAt
            it[finishedAt] = game.finishedAt
        }
    }

    override fun findById(id: UUID): Game {
        return GameTable.selectAll()
                .where { GameTable.id eq id }
                .map { it.toGame() }
                .first()
    }

    private fun ResultRow.toGame(): Game {
        val gameId = this[GameTable.id]
        return when (this[GameTable.gameType]) {
            GameType.TOURNAMENT -> TournamentGame(
                    id = gameId,
                    buyIn = this[GameTable.buyIn],
                    stack = this[GameTable.stack],
                    createdAt = this[GameTable.createdAt],
                    finishedAt = this[GameTable.finishedAt],
                    players = lazy { playerRepo.findPlayers(gameId, TournamentPlayer::class) }.value,
                    finalePlaces = lazy { finalePlacesRepo.findById(gameId) }.value,
                    prizePool = lazy { prizePoolRepo.findById(gameId) }.value,
            )

            GameType.BOUNTY -> BountyTournamentGame(
                    id = gameId,
                    buyIn = this[GameTable.buyIn],
                    bounty = this[GameTable.bounty] ?: error("Bounty not found"),
                    stack = this[GameTable.stack],
                    createdAt = this[GameTable.createdAt],
                    finishedAt = this[GameTable.finishedAt],
                    players = lazy { playerRepo.findPlayers(gameId, BountyPlayer::class) }.value,
                    finalePlaces = lazy { finalePlacesRepo.findById(gameId) }.value,
                    prizePool = lazy { prizePoolRepo.findById(gameId) }.value,
            )

            GameType.CASH -> CashGame(
                    id = gameId,
                    buyIn = this[GameTable.buyIn],
                    stack = this[GameTable.stack],
                    createdAt = this[GameTable.createdAt],
                    finishedAt = this[GameTable.finishedAt],
                    players = lazy { playerRepo.findPlayers(gameId, CashPlayer::class) }.value,
            )
        }
    }

    private fun Game.toType(): GameType =
            when (this) {
                is CashGame -> GameType.CASH
                is BountyTournamentGame -> GameType.BOUNTY
                is TournamentGame -> GameType.TOURNAMENT
                else -> error("Unkown game type ${this::class.simpleName}")
            }
}