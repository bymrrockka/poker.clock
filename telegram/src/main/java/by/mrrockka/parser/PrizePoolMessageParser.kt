package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PositionPrize
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PrizePoolMessageParser : MessageParser<List<PositionPrize>> {

    private val prizePoolRegex = "^(?<place>\\d+)([ .]{1,2})(?<percentage>[0-9]{1,10})(%|)$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): List<PositionPrize> {
        val prizePool = prizePoolRegex.findAll(metadata.text.replace(", ", "\n").trimIndent())
                .associate { it.groups["place"]?.value to it.groups["percentage"]?.value }
                .filter { it.value != null || it.key != null }
                .map { PositionPrize(it.key!!.trim().toInt(), BigDecimal(it.value!!.trim())) }
                .sortedBy { it.position }

        check(prizePool.isNotEmpty()) { "Example: /prize_pool 1. 100% (, #position percentage%)" }
        prizePool.forEachIndexed { index, positionPrize ->
            check(positionPrize.position == index + 1) { "Missed $index place" }
        }
        return prizePool
    }
}
