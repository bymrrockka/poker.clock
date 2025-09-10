package by.mrrockka.repo

import by.mrrockka.domain.FinalPlace
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface FinalePlacesRepo {
    fun findById(gameId: UUID): List<FinalPlace>
    fun store(gameId: UUID, finalePlaces: List<FinalPlace>)
}

@Repository
@Transactional
open class FinalePlacesRepoImpl(
        private val personRepo: PersonRepo,
) : FinalePlacesRepo {
    override fun findById(gameId: UUID): List<FinalPlace> {
        return FinalePlacesTable.selectAll()
                .where { FinalePlacesTable.gameId eq gameId }
                .map { it.toFinalPlace() }
    }

    override fun store(gameId: UUID, finalePlaces: List<FinalPlace>) {
        FinalePlacesTable.batchInsert(finalePlaces) {
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
