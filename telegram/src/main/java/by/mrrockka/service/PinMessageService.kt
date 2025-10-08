package by.mrrockka.service

import by.mrrockka.repo.PinMessageRepo
import by.mrrockka.repo.PinType
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.chat.unpinChatMessage
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.types.msg.Message
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface PinMessageService {
    fun pin(message: Message, pinType: PinType)
    fun unpinAll(message: Message, pinType: PinType)
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class PinMessageServiceImpl(
        private val bot: TelegramBot,
        private val pinMessageRepo: PinMessageRepo,
) : PinMessageService {

    override fun pin(message: Message, pinType: PinType) {
        runBlocking {
            pinChatMessage(message.messageId)
                    .sendReturning(to = message.chat.id, bot)
                    .onFailure { error("Failed to pin message \n ${message.text}") }
                    .also { pinMessageRepo.store(message, pinType) }
        }
    }

    override fun unpinAll(message: Message, pinType: PinType) {
        runBlocking {
            val messageIds = pinMessageRepo.selectByChat(message.chat.id, pinType)
            messageIds
                    .forEach { messageId -> unpinChatMessage(messageId).send(to = message.chat.id, bot) }
            pinMessageRepo.delete(messageIds)
        }
    }
}
