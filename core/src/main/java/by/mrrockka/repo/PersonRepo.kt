package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.Person
import by.mrrockka.repo.PersonTable.firstName
import by.mrrockka.repo.PersonTable.id
import by.mrrockka.repo.PersonTable.lastName
import by.mrrockka.repo.PersonTable.nickName
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PersonRepo {

    fun findById(id: UUID): Person? {
        return PersonTable.selectAll()
                .where { PersonTable.id eq id }
                .map { it.toPerson() }
                .firstOrNull()
    }

    fun findByIds(ids: Set<UUID>): List<Person> {
        return PersonTable.selectAll()
                .where { id inList ids }
                .map { it.toPerson() }
    }

    private fun ResultRow.toPerson(): BasicPerson {
        return BasicPerson(
                id = this[id],
                firstname = this[firstName],
                lastname = this[lastName],
                nickname = this[nickName],
        )
    }

    fun findByNicknames(nicknames: List<String>): List<Person> {
        return PersonTable.selectAll()
                .where { nickName inList nicknames }
                .map { it.toPerson() }
    }

    fun upsertBatch(persons: List<Person>) {
        PersonTable.batchUpsert(persons) { person ->
            this[id] = person.id
            this[firstName] = person.firstname
            this[lastName] = person.lastname
            this[nickName] = person.nickname
        }
    }
}
