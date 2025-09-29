package by.mrrockka.service

import by.mrrockka.BotCommands
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.HelpMessageParser
import org.springframework.stereotype.Service

interface HelpTelegramService {
    fun help(metadata: MessageMetadata): BotCommands.Description
}

@Service
class HelpTelegramServiceImpl(
        private val descriptions: BotCommands,
        private val helpMessageParser: HelpMessageParser,
) : HelpTelegramService {

    override fun help(metadata: MessageMetadata): BotCommands.Description {
        val description = helpMessageParser.parse(metadata)
                .let { text -> descriptions.byNameAndAlias[text] }

        return description ?: descriptions.byNameAndAlias["help"]!!
    }
}
