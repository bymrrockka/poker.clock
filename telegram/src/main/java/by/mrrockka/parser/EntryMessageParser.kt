package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@RequiredArgsConstructor
class EntryMessageParser {
    private val nicknameRegex = "^@(?<username>[\\d\\w_-]{5,})$".toRegex(RegexOption.MULTILINE)
    private val amountRegex = "^(?<amount>[\\d]+)$".toRegex(RegexOption.MULTILINE)

    fun parse(metadata: MessageMetadata): Pair<Set<String>, BigDecimal?> {
        val command = metadata.text.replace(" ", "\n").trimIndent()
        val nicknames = nicknameRegex.findAll(command)
                .mapNotNull { it.groups["username"]?.value }
                .toSet()
        check(nicknames.isNotEmpty()) { "No nickname found" }
        val amount = amountRegex.find(command)?.value?.let { BigDecimal(it) }
        return nicknames to amount
    }

}
