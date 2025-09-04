package by.mrrockka.service

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.Person
import by.mrrockka.repo.ChatPersonsRepo
import by.mrrockka.repo.PersonRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface TelegramPersonService {
    fun findByMessage(messageMetadata: MessageMetadata): List<UUID>
}

@Component
@Transactional
open class TelegramPersonServiceImpl(
        private val personRepo: PersonRepo,
        private val chatPersonsRepo: ChatPersonsRepo,
) : TelegramPersonService {

    override fun findByMessage(messageMetadata: MessageMetadata): List<UUID> {
        val nicknames = messageMetadata.mentions.map { it.text }
        val persons = personRepo.findByNicknames(nicknames)
        val newNicknameToId = nicknames.newNicknames(persons)
                .let { nicknames ->
                    val map = nicknames.associate { it to UUID.randomUUID() }
                    personRepo.upsertBatch(map.map { (nickname, id) -> BasicPerson(nickname = nickname, id = id) })
                    map
                }

        val personIds = persons.map { it.id } + newNicknameToId.map { it.value }
        chatPersonsRepo.insertBatch(personIds, messageMetadata.chatId)

        return personIds
    }

    private fun List<String>.newNicknames(existing: List<Person>): List<String> {
        val existingNicknames = existing.map { it.nickname }
        return filter { !existingNicknames.contains(it) }
    }

}