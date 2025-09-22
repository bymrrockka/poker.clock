package by.mrrockka.repo

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Game
import by.mrrockka.domain.GameType
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.TournamentPlayer
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface GameRepo {
    fun store(game: Game)
    fun findById(id: UUID): Game
    fun findByIds(ids: List<UUID>): List<Game>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class GameRepoImpl(
        private val playerRepo: PlayerRepo,
        private val finalePlacesRepo: FinalePlacesRepo,
        private val prizePoolRepo: PrizePoolRepo,
) : GameRepo {

    override fun store(game: Game) {
        GameTable.upsert {
            it[id] = game.id
            it[gameType] = game.toType()
            it[buyIn] = game.buyIn
            it[stack] = game.stack
            it[bounty] = game.let { if (it is BountyTournamentGame) it.bounty else null }
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

    override fun findByIds(ids: List<UUID>): List<Game> {
        return GameTable.selectAll()
                .where { GameTable.id inList ids }
                .map { it.toGame() }
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
                    playersProvider = { playerRepo.findPlayers(gameId, TournamentPlayer::class) },
                    finalePlacesProvider = { finalePlacesRepo.findById(gameId) },
                    prizePoolProvider = { prizePoolRepo.findById(gameId) },
            )

            GameType.BOUNTY -> BountyTournamentGame(
                    id = gameId,
                    buyIn = this[GameTable.buyIn],
                    bounty = this[GameTable.bounty] ?: error("Bounty not found"),
                    stack = this[GameTable.stack],
                    createdAt = this[GameTable.createdAt],
                    finishedAt = this[GameTable.finishedAt],
                    playersProvider = { playerRepo.findPlayers(gameId, BountyPlayer::class) },
                    finalePlacesProvider = { finalePlacesRepo.findById(gameId) },
                    prizePoolProvider = { prizePoolRepo.findById(gameId) },
            )

            GameType.CASH -> CashGame(
                    id = gameId,
                    buyIn = this[GameTable.buyIn],
                    stack = this[GameTable.stack],
                    createdAt = this[GameTable.createdAt],
                    finishedAt = this[GameTable.finishedAt],
                    playersProvider = { playerRepo.findPlayers(gameId, CashPlayer::class) },
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