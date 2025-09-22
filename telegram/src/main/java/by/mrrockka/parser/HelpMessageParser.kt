package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component

@Component
class HelpMessageParser : MessageParser<String?> {
    private val helpRegex = "^/help([ ]*)(.*)$".toRegex()

    override fun parse(metadata: MessageMetadata): String? {
        return metadata.text.trim().let { helpRegex.find(it)?.groups?.get(2)?.value }
    }
}