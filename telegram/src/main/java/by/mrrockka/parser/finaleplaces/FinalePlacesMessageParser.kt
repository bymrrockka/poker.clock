package by.mrrockka.parser.finaleplaces

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component
import kotlin.text.RegexOption.MULTILINE

@Component
class FinalePlacesMessageParser {

    private val finalPlaceRegex = "^(?<place>\\d+)([ .]{1,})(@(?<username>[A-z0-9_-]{5,}))$".toRegex(MULTILINE)

    fun parse(messageMetadata: MessageMetadata): Map<Int, String> {
        val finalePlaces = finalPlaceRegex.findAll(messageMetadata.text.replace("([, ]+)(?![A-z@])".toRegex(), "\n").trimIndent())
                .associate { it.groups["place"]?.value to it.groups["username"]?.value }
                .filter { it.value != null || it.key != null }
                .map { it.key!!.trim().toInt() to it.value!!.trim() }
                .sortedBy { it.first }
                .toMap()

        check(finalePlaces.size != 0) { "/finaleplaces 1 @nickname (, #position @nickname)" }
        check(finalePlaces.size == messageMetadata.entities.size) { "Finale places do not match mentions size." }
        (1..finalePlaces.size).forEach { index ->
            check(finalePlaces[index] != null) { "Missed $index place" }
        }

        return finalePlaces
    }

}
