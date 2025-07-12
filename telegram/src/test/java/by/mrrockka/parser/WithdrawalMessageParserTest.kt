package by.mrrockka.parser

import by.mrrockka.AbstractTest
import by.mrrockka.builder.mentions
import by.mrrockka.builder.message
import by.mrrockka.builder.messageEntity
import by.mrrockka.domain.MessageMetadata
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

//todo: refactor
class WithdrawalMessageParserTest : AbstractTest() {
    private val withdrawalMessageParser = WithdrawalMessageParser()

    data class WithdrawalArgument(
            val metadata: MessageMetadata,
            val nicknames: Set<String>,
            val amount: BigDecimal
    )

    @ParameterizedTest
    @MethodSource("withdrawalWithMentionsMessage")
    fun givenEntryMessageWithMentions_whenParseAttempt_shouldParseToPair(argument: WithdrawalArgument) {
        val (amount, nicknames) = withdrawalMessageParser.parse(argument.metadata)
        assertThat(amount).isEqualTo(argument.amount)
        assertThat(nicknames).isEqualTo(argument.nicknames)
    }

    @ParameterizedTest
    @MethodSource("invalidEntryWithMentionsMessage")
    fun givenInvalidEntryMessageWithMentions_whenParseAttempt_shouldThrowException(message: String) {
        val metadata = message { this.text = message }
        assertThrows<IllegalStateException> { withdrawalMessageParser.parse(metadata) }
    }

    companion object {
        const val BOT_NAME: String = "pokerbot"

        @JvmStatic
        private fun withdrawalWithMentionsMessage(): List<WithdrawalArgument> {
            return listOf(
                    WithdrawalArgument(
                            metadata = message {
                                this.text = "/withdrawal @kinger 60"
                                entity(messageEntity { this.text = "@kinger" }.mention())
                            },
                            nicknames = setOf("kinger"),
                            amount = BigDecimal("60")
                    ),
                    WithdrawalArgument(
                            metadata = message {
                                this.text = "/withdrawal 60 @kinger"
                                entity(messageEntity { this.text = "@kinger" }.mention())
                            },
                            nicknames = setOf("kinger"),
                            amount = BigDecimal("60")
                    ),
                    WithdrawalArgument(
                            metadata = message {
                                this.text = "/withdrawal @kinger @asadf @asdfasdf @koomko 30"
                                this.entities = listOf("@kinger", "@asadf", "@asdfasdf", "@koomko").mentions()
                            },
                            nicknames = setOf("kinger", "asadf", "asdfasdf", "koomko"),
                            amount = BigDecimal("30")
                    ),
            )
        }

        @JvmStatic
        private fun invalidEntryWithMentionsMessage(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("/withdrawal @kinger"),
                    Arguments.of("/withdrawal@kinger"),
                    Arguments.of("/withdrawal@$BOT_NAME"),
                    Arguments.of("/withdrawal"),
                    Arguments.of("@kinger/withdrawal")
            )
        }
    }
}