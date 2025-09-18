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
    fun findByMessage(messageMetadata: MessageMetadata): List<Person>
}

@Component
@Transactional(propagation = Propagation.REQUIRED)
open class TelegramPersonServiceImpl(
        private val personRepo: PersonRepo,
        private val chatPersonsRepo: ChatPersonsRepo,
) : TelegramPersonService {

    override fun findByMessage(messageMetadata: MessageMetadata): List<Person> {
        val nicknames = messageMetadata.mentions.map { it.text }
        val persons = personRepo.findByNicknames(nicknames)
        val newPersons = nicknames.newNicknames(persons)
                .let { nicknames ->
                    val newPersons = nicknames.map { nickname -> BasicPerson(nickname = nickname, id = UUID.randomUUID()) }
                    personRepo.upsertBatch(newPersons)
                    newPersons
                }

        val allPersons = persons + newPersons
        chatPersonsRepo.insertBatch(allPersons.map { it.id }, messageMetadata.chatId)

        return allPersons
    }

    private fun List<String>.newNicknames(existing: List<Person>): List<String> {
        val existingNicknames = existing.map { it.nickname }
        return filter { !existingNicknames.contains(it) }
    }

}