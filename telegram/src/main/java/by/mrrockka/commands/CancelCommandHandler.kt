package by.mrrockka.commands

import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.Guard
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface CancelCommandHandler {
    suspend fun cancel(message: MessageUpdate)
}

@Component
class CancelCommandHandlerImpl() : CancelCommandHandler {

    @CommandHandler(["/cancel"])
    @Guard(AdminGuard::class)
    override suspend fun cancel(message: MessageUpdate) {
        TODO("Not yet implemented")
    }

}