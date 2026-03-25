package by.mrrockka.commands

import by.mrrockka.domain.chat
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.getChatMember
import eu.vendeli.tgbot.interfaces.helper.Guard
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chat.ChatMember
import eu.vendeli.tgbot.types.component.ProcessedUpdate
import eu.vendeli.tgbot.types.component.onFailure

object AdminGuard : Guard {
    override suspend fun condition(user: User?, update: ProcessedUpdate, bot: TelegramBot): Boolean {
        if (user == null) error("User can't be null")

        if (user.isBot) error("Bot user is not allowed")

        getChatMember(user)
                .sendReturning(update.chat(), bot)
                .onFailure { error("Can't get permission for user @${user.username}. Check bot permissions for channel (it should be admin)") }
                .also { member ->
                    if (member !is ChatMember.Administrator && member !is ChatMember.Owner) error("Only administrators allowed to user this command.")
                }

        return true
    }
}