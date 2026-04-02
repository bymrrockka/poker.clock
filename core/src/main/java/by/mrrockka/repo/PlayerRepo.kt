package by.mrrockka.repo

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.KClass

interface PlayerRepo {
    fun <T : Player> findPlayers(gameId: UUID, clazz: KClass<T>): List<T>
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PlayerRepoImpl(
        val personRepo: PersonRepo,
        val entriesRepo: EntriesRepo,
        val withdrawalsRepo: WithdrawalsRepo,
        val bountyRepo: BountyRepo,
) : PlayerRepo {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Player> findPlayers(gameId: UUID, clazz: KClass<T>): List<T> {
        val entries = entriesRepo.findByGame(gameId)
        val withdrawals = withdrawalsRepo.findByGame(gameId)
        val bounties = bountyRepo.findByGame(gameId)

        return personRepo.findByIds(entries.keys).map { person ->
            val entries = entries[person.id] ?: error("No entries found for ${person.nickname}")
            when (clazz) {
                TournamentPlayer::class -> {
                    TournamentPlayer(
                            person = person,
                            entries = entries,
                    )
                }

                CashPlayer::class -> {
                    CashPlayer(
                            person = person,
                            entries = entries,
                            withdrawals = withdrawals[person.id] ?: emptyList(),
                    )
                }

                BountyPlayer::class -> {
                    BountyPlayer(
                            person = person,
                            entries = entries,
                            bounties = bounties.filter { it.from == person || it.to == person },
                    )
                }

                else -> error("No such player type ${clazz.simpleName}")
            } as T
        }.sortedBy { it.person.nickname }
    }
}
