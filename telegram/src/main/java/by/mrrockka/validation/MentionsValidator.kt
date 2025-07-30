package by.mrrockka.validation

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.mesageentity.MessageEntityType
import org.springframework.stereotype.Component

@Component
class MentionsValidator {

    fun validateMentions(messageMetadata: MessageMetadata) {
        check(messageMetadata.mentions().isNotEmpty()) { "Message should contain at least one person mention" }
        val textMention = messageMetadata.entities.find { it.type == MessageEntityType.TEXT_MENTION }
        check(textMention == null) { "Can't register players without nickname. ${textMention!!.text()}" }
    }
}