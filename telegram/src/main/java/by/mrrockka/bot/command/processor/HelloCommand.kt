package by.mrrockka.bot.command.processor

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Component

interface HelloCommand {
    suspend fun hello(user: User, bot: TelegramBot)
}

@Component
class HelloCommandImpl : HelloCommand {

    @CommandHandler(["/hello"])
    override suspend fun hello(user: User, bot: TelegramBot) {
        sendMessage { "Hello" }.send(user, bot)
    }
}