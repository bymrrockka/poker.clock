package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class EntryMessageParser: MessageParser<Pair<Set<String>, BigDecimal?>> {
    private val amountRegex = "^(?<amount>[\\d]+)$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): Pair<Set<String>, BigDecimal?> {
        val command = metadata.text.replace(" ", "\n").trimIndent()
        check(metadata.mentions.isNotEmpty()) { "No mentions found" }
        val amount = amountRegex.find(command)?.value?.let { BigDecimal(it) }
        return metadata.mentions.map { it.text }.toSet() to amount
    }

}
