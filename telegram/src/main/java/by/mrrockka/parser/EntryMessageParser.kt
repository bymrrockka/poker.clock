package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@RequiredArgsConstructor
class EntryMessageParser {
    private val amountRegex = "^(?<amount>[\\d]+)$".toRegex(RegexOption.MULTILINE)

    fun parse(metadata: MessageMetadata): Pair<Set<String>, BigDecimal?> {
        val command = metadata.text.replace(" ", "\n").trimIndent()
        check(metadata.mentions.isNotEmpty()) { "No mentions found" }
        val amount = amountRegex.find(command)?.value?.let { BigDecimal(it) }
        return metadata.mentions.map { it.text }.toSet() to amount
    }

}
