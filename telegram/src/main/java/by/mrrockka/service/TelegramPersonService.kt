package by.mrrockka.service

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Person
import by.mrrockka.repo.ChatPersonsRepo
import by.mrrockka.repo.PersonRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface TelegramPersonService {
    fun findByMessage(metadata: MessageMetadata): List<Person>
    fun findByFrom(metadata: MessageMetadata): Person
}

@Component
@Transactional(propagation = Propagation.REQUIRED)
open class TelegramPersonServiceImpl(
        private val personRepo: PersonRepo,
        private val chatPersonsRepo: ChatPersonsRepo,
) : TelegramPersonService {

    override fun findByMessage(metadata: MessageMetadata): List<Person> {
        val nicknames = metadata.mentions.map { it.text }
        val persons = personRepo.findByNicknames(nicknames)
        val newPersons = nicknames.newNicknames(persons)
                .let { nicknames ->
                    val newPersons = nicknames.map { nickname -> BasicPerson(nickname = nickname, id = UUID.randomUUID()) }
                    personRepo.store(newPersons)
                    newPersons
                }

        val allPersons = persons + newPersons
        chatPersonsRepo.store(allPersons.map { it.id }, metadata.chatId)

        return allPersons
    }

    override fun findByFrom(metadata: MessageMetadata): Person {
        check(metadata.from?.username != null) { "User must have nickname to execute command" }
        val person = personRepo.findByNicknames(listOf(metadata.from.username!!)).firstOrNull()
        check(person != null) { "Person does not found" }
        check(chatPersonsRepo.personChats(person.id).contains(metadata.chatId)) { "Person in not found in this chat" }
        return person
    }

    private fun List<String>.newNicknames(existing: List<Person>): List<String> {
        val existingNicknames = existing.map { it.nickname }
        return filter { !existingNicknames.contains(it) }
    }

}