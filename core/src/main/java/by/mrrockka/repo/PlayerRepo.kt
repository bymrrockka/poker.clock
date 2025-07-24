package by.mrrockka.repo

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.KClass

interface PlayerRepo {
    fun <T : Player> findPlayers(gameId: UUID, clazz: KClass<T>): List<T>
}

@Repository
@Transactional
open class PlayerRepoImpl(
        val personRepo: PersonRepo,
        val entriesRepo: EntriesRepo,
        val withdrawalsRepo: WithdrawalsRepo,
        val bountyRepo: BountyRepo,
) : PlayerRepo {

    override fun <T : Player> findPlayers(gameId: UUID, clazz: KClass<T>): List<T> {
        val personEntries = entriesRepo.findGameEntries(gameId)

        return personRepo.findByIds(personEntries.keys).map { person ->
            val entries = personEntries[person.id] ?: error("No entries found for ${person.nickname}")
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
                            withdrawals = withdrawalsRepo.findByPerson(gameId, person.id),
                    )
                }

                BountyPlayer::class -> {
                    BountyPlayer(
                            person = person,
                            entries = entries,
                            bounties = bountyRepo.findByPerson(gameId, person.id),
                    )
                }

                else -> error("No such player type ${clazz.simpleName}")
            } as T
        }
    }
}
