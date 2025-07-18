package by.mrrockka.repo

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Player
import by.mrrockka.domain.TournamentPlayer
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class PlayerRepo(
        val personRepo: PersonRepo,
        val entriesRepo: EntriesRepo,
        val withdrawalsRepo: WithdrawalsRepo,
        val bountyRepo: BountyRepo,
) {

    inline fun <reified T : Player> findPlayers(gameId: UUID): List<T> {
        val personEntries = entriesRepo.findGameEntries(gameId)

        return personRepo.findByIds(personEntries.keys).map { person ->
            when {
                T::class.java.isAssignableFrom(TournamentPlayer::class.java) -> {
                    TournamentPlayer(
                            person = person,
                            entries = personEntries[person.id] ?: error("No entries found for ${person.nickname}"),
                    )
                }

                T::class.java.isAssignableFrom(CashPlayer::class.java) -> {
                    CashPlayer(
                            person = person,
                            entries = personEntries[person.id] ?: error("No entries found for ${person.nickname}"),
                            withdrawals = withdrawalsRepo.findByPerson(person.id),
                    )
                }

                T::class.java.isAssignableFrom(BountyPlayer::class.java) -> {
                    BountyPlayer(
                            person = person,
                            entries = personEntries[person.id] ?: error("No entries found for ${person.nickname}"),
                            bounties = bountyRepo.findByPerson(person.id),
                    )
                }

                else -> error("No such player type ${T::class.java.simpleName}")
            } as T
        }
    }
}
