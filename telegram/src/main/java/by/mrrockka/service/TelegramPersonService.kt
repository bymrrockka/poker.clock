package by.mrrockka.service

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.repo.ChatPersonsRepo
import by.mrrockka.repo.PersonRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface TelegramPersonService {
    fun findByMessage(metadata: MessageMetadata): List<BasicPerson>
    fun findOrAdd(metadata: MessageMetadata): List<BasicPerson>
    fun findOrAdd(nicknames: List<String>, chatId: Long): List<BasicPerson>
    fun findOrAdd(nickname: String, chatId: Long): BasicPerson
    fun findByFrom(metadata: MessageMetadata): BasicPerson
}

@Component
@Transactional(propagation = Propagation.REQUIRED)
open class TelegramPersonServiceImpl(
        private val personRepo: PersonRepo,
        private val chatPersonsRepo: ChatPersonsRepo,
) : TelegramPersonService {

    override fun findByMessage(metadata: MessageMetadata): List<BasicPerson> {
        val nicknames = metadata.mentions.map { it.text }
        return personRepo.findByNicknames(nicknames)
    }

    override fun findOrAdd(metadata: MessageMetadata): List<BasicPerson> = findOrAdd(metadata.mentions.map { it.text }, metadata.chatId)

    override fun findOrAdd(nicknames: List<String>, chatId: Long): List<BasicPerson> {
        val persons = personRepo.findByNicknames(nicknames)
        val newPersons = nicknames.newNicknames(persons)
                .let { nicknames ->
                    val newPersons = nicknames.map { nickname -> BasicPerson(nickname = nickname, id = UUID.randomUUID()) }
                    personRepo.store(newPersons)
                    newPersons
                }

        val allPersons = persons + newPersons
        chatPersonsRepo.store(allPersons.map { it.id }, chatId)

        return allPersons
    }

    override fun findOrAdd(nickname: String, chatId: Long): BasicPerson {
        return personRepo.findByNickname(nickname)
                .let { person ->
                    if (person != null) person
                    else {
                        val new = BasicPerson(nickname = nickname, id = UUID.randomUUID())
                        personRepo.store(new)
                        chatPersonsRepo.store(new.id, chatId)
                        new
                    }
                }
    }

    override fun findByFrom(metadata: MessageMetadata): BasicPerson {
        check(metadata.from?.username != null) { "User must have nickname to execute command" }
        val person = personRepo.findByNickname(metadata.from.username!!)
        check(person != null) { "Person does not found" }
        check(chatPersonsRepo.personChats(person.id).contains(metadata.chatId)) { "Person in not found in this chat" }
        return person
    }

    private fun List<String>.newNicknames(existing: List<BasicPerson>): List<String> {
        val existingNicknames = existing.map { it.nickname }
        return filter { !existingNicknames.contains(it) }
    }

}