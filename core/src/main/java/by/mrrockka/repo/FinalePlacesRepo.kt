package by.mrrockka.repo

import by.mrrockka.domain.FinalPlace
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface FinalePlacesRepo {
    fun findById(gameId: UUID): List<FinalPlace>
    fun store(gameId: UUID, finalePlaces: List<FinalPlace>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class FinalePlacesRepoImpl(
        private val personRepo: PersonRepo,
) : FinalePlacesRepo {
    override fun findById(gameId: UUID): List<FinalPlace> {
        return FinalePlacesTable.selectAll()
                .where { FinalePlacesTable.gameId eq gameId }
                .map { it.toFinalPlace() }
    }

    override fun store(gameId: UUID, finalePlaces: List<FinalPlace>) {
        FinalePlacesTable.batchUpsert(keys = arrayOf(FinalePlacesTable.gameId, FinalePlacesTable.position), data = finalePlaces) {
            this[FinalePlacesTable.gameId] = gameId
            this[FinalePlacesTable.personId] = it.person.id
            this[FinalePlacesTable.position] = it.position
        }
    }

    private fun ResultRow.toFinalPlace(): FinalPlace {
        return FinalPlace(
                position = this[FinalePlacesTable.position],
                person = lazy { personRepo.findById(this[FinalePlacesTable.personId]) }.value
                        ?: error("Can't find person with ${this[FinalePlacesTable.personId]} id"),
        )
    }

}
