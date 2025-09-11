package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component

@Component
class BountyMessageParser : MessageParser<Pair<String, String>> {
    private val bountyRegex = "^(@(?<to>[A-z0-9_-]{5,}|me)) +kicked +(@(?<from>[A-z0-9_-]{5,}|me))$".toRegex()

    override fun parse(metadata: MessageMetadata): Pair<String, String> {
        val command = metadata.text.replace("/([\\w]+) ".toRegex(), "\n").trim()
        check(metadata.mentions.isNotEmpty()) { "No mentions found" }
        val pair = bountyRegex.find(command)?.groups
                .let {
                    check(it?.get("to")?.value != null || it?.get("from")?.value != null) { "Example /bounty @to kicked @from" }
                    it["from"]!!.value.ifMe(metadata) to it["to"]!!.value.ifMe(metadata)
                }
        return pair
    }
}