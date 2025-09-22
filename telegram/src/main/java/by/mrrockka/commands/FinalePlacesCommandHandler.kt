package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.FinalePlacesTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface FinalePlacesCommandHandler {
    suspend fun store(message: MessageUpdate)
}

@Component
class FinalePlacesCommandHandlerImpl(
        private val finalePlacesService: FinalePlacesTelegramService,
        private val bot: TelegramBot,
) : FinalePlacesCommandHandler {

    @CommandHandler(["/finale_places", "/fp"])
    override suspend fun store(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        finalePlacesService.store(metadata)
                .also { finalePlaces ->
                    sendMessage {
                        """
                        |Finale places stored:
                        |${finalePlaces.joinToString("\n") { "${it.position}. @${it.person.nickname}" }}
                        """.trimMargin()
                    }.send(to = metadata.chatId, via = bot)
                }
    }
}