package by.mrrockka.parser

import by.mrrockka.domain.MessageMetadata
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WithdrawalMessageParser : MessageParser<BigDecimal> {
    private val amountRegex = "^(?<amount>[\\d]+)$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): BigDecimal {
        val command = metadata.text.replace(" ", "\n").trimIndent()
        return amountRegex.find(command)?.value?.let { BigDecimal(it) } ?: error("Amount should be provided")
    }
}
