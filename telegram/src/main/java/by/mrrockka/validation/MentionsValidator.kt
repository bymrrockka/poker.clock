package by.mrrockka.validation

import by.mrrockka.domain.MessageMetadata
import eu.vendeli.tgbot.types.msg.EntityType
import org.springframework.stereotype.Component

@Component
class MentionsValidator {

    fun validateMentions(messageMetadata: MessageMetadata) {
        check(messageMetadata.mentions.isNotEmpty()) { "Message should contain at least one person mention" }
        val textMention = messageMetadata.entities.find { it.type == EntityType.TextMention }
        check(textMention == null) { "Can't register players without nickname. Text mentions are not allowed" }
    }
}