package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WithdrawalMessageParser : MessageParser<Pair<Set<String>, BigDecimal>> {
    private val nicknameRegex = "^@(?<username>[\\d\\w_-]{5,})$".toRegex(RegexOption.MULTILINE)
    private val amountRegex = "^(?<amount>[\\d]+)$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): Pair<Set<String>, BigDecimal> {
        val command = metadata.text.replace(" ", "\n").trimIndent()
        val nicknames = nicknameRegex.findAll(command)
                .mapNotNull { it.groups["username"]?.value }
                .toSet()
        check(nicknames.isNotEmpty()) { "No nickname found" }
        val amount = amountRegex.find(command)?.value?.let { BigDecimal(it) } ?: error("Amount should be provided")
        return nicknames to amount
    }
}
