package by.mrrockka.repo

import by.mrrockka.domain.BasicPerson
import by.mrrockka.repo.PersonTable.id
import by.mrrockka.repo.PersonTable.nickName
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.batchUpsert
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface PersonRepo {
    fun findById(id: UUID): BasicPerson?
    fun findByIds(ids: Set<UUID>): List<BasicPerson>
    fun findByNicknames(nicknames: List<String>): List<BasicPerson>
    fun findByNickname(nickname: String): BasicPerson?
    fun store(person: BasicPerson)
    fun store(persons: List<BasicPerson>)
}

@Repository
@Transactional(propagation = Propagation.REQUIRED)
open class PersonRepoImpl : PersonRepo {

    override fun findById(id: UUID): BasicPerson? {
        return findByIds(setOf(id)).firstOrNull()
    }

    override fun findByIds(ids: Set<UUID>): List<BasicPerson> {
        return PersonTable.selectAll()
                .where { id inList ids }
                .map { it.toPerson() }
    }

    private fun ResultRow.toPerson(): BasicPerson {
        return BasicPerson(
                id = this[id],
                nickname = this[nickName],
        )
    }

    override fun findByNicknames(nicknames: List<String>): List<BasicPerson> {
        return PersonTable.selectAll()
                .where { nickName inList nicknames }
                .map { it.toPerson() }
    }

    override fun findByNickname(nickname: String): BasicPerson? {
        return findByNicknames(listOf(nickname)).firstOrNull()
    }

    override fun store(person: BasicPerson) {
        PersonTable.insert {
            it[id] = person.id
            it[nickName] = person.nickname
        }
    }

    override fun store(persons: List<BasicPerson>) {
        PersonTable.batchUpsert(data = persons) { person ->
            this[id] = person.id
            this[nickName] = person.nickname
        }
    }
}
