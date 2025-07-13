package by.mrrockka.repo

import by.mrrockka.domain.FinalPlace
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class FinalePlacesRepo(
        private val personRepo: PersonRepo,
) {
    fun findById(gameId: UUID): List<FinalPlace> {
        return FinalePlacesTable.selectAll()
                .where { FinalePlacesTable.gameId eq gameId }
                .map { it.toFinalPlace() }
    }

    private fun ResultRow.toFinalPlace(): FinalPlace {
        return FinalPlace(
                position = this[FinalePlacesTable.position],
                person = lazy { personRepo.findById(this[FinalePlacesTable.personId]) }.value
                        ?: error("Can't find person with ${this[FinalePlacesTable.personId]} id"),
        )
    }

}
