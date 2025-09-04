package by.mrrockka.bot.command

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.component.ProcessedUpdate
import org.springframework.stereotype.Component

//todo remove
@Deprecated("Was created only for framework setup")
interface HelloCommandHandler {
    suspend fun hello(update: ProcessedUpdate, user: User, bot: TelegramBot)
}

@Component
class HelloCommandHandlerImpl : HelloCommandHandler {

    @CommandHandler(["/hello"])
    override suspend fun hello(update: ProcessedUpdate, user: User, bot: TelegramBot) {
        sendMessage { "Hello" }.send(user, bot)
    }
}