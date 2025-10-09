package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.FinalePlacesTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.stereotype.Component

interface FinalePlacesCommandHandler {
    suspend fun store(message: MessageUpdate)
}

@Component
class FinalePlacesCommandHandlerImpl(
        private val bot: TelegramBot,
        private val finalePlacesService: FinalePlacesTelegramService,
        private val pinMessageService: PinMessageService,
) : FinalePlacesCommandHandler {

    @CommandHandler(["/finale_places", "/fp"])
    override suspend fun store(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        finalePlacesService.store(metadata)
                .let { finalePlaces ->
                    sendMessage {
                        """
                        |Finale places stored:
                        |${finalePlaces.joinToString("\n") { "${it.position}. @${it.person.nickname}" }}
                        """.trimMargin()
                    }.sendReturning(to = metadata.chatId, via = bot)
                            .onFailure { error("Failed to send finale places message") }
                            ?: error("No message returned from telegram api")
                }.also { message -> pinMessageService.pin(message, PinType.GAME) }
    }
}