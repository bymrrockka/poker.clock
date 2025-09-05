package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import eu.vendeli.tgbot.types.msg.EntityType

fun MessageMetadata.checkMentions() {
    check(mentions.isNotEmpty()) { "Message should contain at least one person mention" }
    val textMention = entities.find { it.type == EntityType.TextMention }
    check(textMention == null) { "Can't register players without nickname. Text mentions are not allowed. (${text.substring(textMention!!.offset, textMention.length)})" }
}