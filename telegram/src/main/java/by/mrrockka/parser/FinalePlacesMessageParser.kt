package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component

@Component
class FinalePlacesMessageParser : MessageParser<Map<Int, String>> {
    private val finalPlaceRegex = "^(?<place>\\d+)([ .]{1,})(@(?<username>[A-z0-9_-]{5,}|me))$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): Map<Int, String> {
        val finalePlaces = finalPlaceRegex.findAll(metadata.text.replace("([, ]+)(?![A-z@])".toRegex(), "\n").trimIndent())
                .associate { it.groups["place"]?.value to it.groups["username"]?.value }
                .filter { it.value != null || it.key != null }
                .map { it.key!!.trim().toInt() to it.value!!.trim().ifMe(metadata) }
                .sortedBy { it.first }
                .toMap()

        check(finalePlaces.size != 0) { "/finale_places 1 @nickname (, #position @nickname)" }
        check(finalePlaces.size == metadata.mentions.size) { "Finale places do not match mentions size." }
        (1..finalePlaces.size).forEach { index ->
            check(finalePlaces[index] != null) { "Missed $index place" }
        }

        return finalePlaces
    }

}