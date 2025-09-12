package by.mrrockka.service

import by.mrrockka.bot.BotDescriptionProperties
import by.mrrockka.bot.CommandDescription
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.parser.HelpMessageParser
import org.springframework.stereotype.Service

interface HelpTelegramService {
    fun help(metadata: MessageMetadata): CommandDescription
}

@Service
class HelpTelegramServiceImpl(
        private val descriptions: BotDescriptionProperties,
        private val helpMessageParser: HelpMessageParser,
) : HelpTelegramService {

    override fun help(metadata: MessageMetadata): CommandDescription {
        val description = helpMessageParser.parse(metadata)
                .let { text -> descriptions.byNamesAndAliases[text] }

        return description ?: descriptions.byNamesAndAliases["help"]!!
    }
}
