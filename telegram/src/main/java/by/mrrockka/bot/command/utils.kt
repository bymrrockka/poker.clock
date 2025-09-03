package by.mrrockka.bot.command

import eu.vendeli.tgbot.types.component.MessageUpdate

fun MessageUpdate.chatid(): Long = this.message.chat.id