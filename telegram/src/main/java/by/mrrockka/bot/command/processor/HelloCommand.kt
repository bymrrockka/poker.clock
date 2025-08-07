package by.mrrockka.bot.command.processor

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.types.User
import org.springframework.stereotype.Component

@Component
class HelloCommand {

    @CommandHandler(["/hello"])
    fun hello(user: User, bot: TelegramBot) {

    }
}