package by.mrrockka.commands

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.EntryTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface EntryCommandHandler {
    suspend fun entry(message: MessageUpdate)
}

@Component
class EntryCommandHandlerImpl(
        private val bot: TelegramBot,
        private val entryTelegramService: EntryTelegramService,
) : EntryCommandHandler {

    @CommandHandler(["/entry", "/reentry"])
    override suspend fun entry(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        //todo: add ability to entry without nickname or @me and decline command handler
        entryTelegramService.entry(metadata)
                .also { (tables, amount) ->
                    message {
                        if (tables.isNotEmpty()) {
                            """
                            |Entries: 
                            ${
                                tables.joinToString("\n") { table ->
                                    """|${"-".repeat(30)}
                                    |Table ${table.id}
                                    |Seats:
                                    ${
                                        table.seats.sortedBy { it.num }
                                                .joinToString("\n") { seat -> "|  @${seat.nickname} seat ${seat.num} -> entry $amount" }
                                    }"""
                                }
                            }""".trimMargin()
                        } else "Entry stored"
                    }.send(to = metadata.chatId, via = bot)
                }
    }
}