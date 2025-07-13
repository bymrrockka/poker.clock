package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.Person
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
open class PersonRepo {

    fun upsert(person: Person) {
        PersonTable.upsert {
            it[id] = person.id
            it[firstName] = person.firstname
            it[lastName] = person.lastname
            it[nickName] = person.nickname
        }
    }

    fun findById(id: UUID): Person? {
        return PersonTable.selectAll()
                .where { PersonTable.id eq id }
                .map { it.toPerson() }
                .firstOrNull()
    }

    fun findByIds(ids: Set<UUID>): List<Person> {
        return PersonTable.selectAll()
                .where { PersonTable.id inList ids }
                .map { it.toPerson() }
    }

    private fun ResultRow.toPerson(): BasicPerson {
        return BasicPerson(
                id = this[PersonTable.id],
                firstname = this[PersonTable.firstName],
                lastname = this[PersonTable.lastName],
                nickname = this[PersonTable.nickName],
        )
    }
}
