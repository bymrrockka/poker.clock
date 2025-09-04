package by.mrrockka.parser

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentGame
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Component
class GameMessageParser {
    private val amountRegex = "(?<amount>[.\\d]+)(?<multiplier>[A-z]{0,1})"
    private val buyInRegex = "^buyin:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val stackRegex = "^stack:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val bountyRegex = "^bounty:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val typeRegex = "([\\w]+)_game$".toRegex(RegexOption.MULTILINE)

    fun parse(messageMetadata: MessageMetadata): Game {
        val type = typeRegex.find(messageMetadata.command.text.trimIndent())!!.destructured
                .let { (match) -> GameType.valueOf(match.uppercase()) }
        val buyin = buyInRegex.find(messageMetadata.text.trimIndent())
                .let { match ->
                    val amount = match?.groups["amount"]?.value
                    val modifier = match?.groups["multiplier"]?.value.multiplierAsDecimal()
                    check(amount != null) { "Buy in should be specified" }
                    BigDecimal(amount) * modifier
                }

        val stack = stackRegex.find(messageMetadata.text.trimIndent())
                .let { match ->
                    if (match != null) {
                        val amount = match.groups["amount"]?.value
                        val modifier = match.groups["multiplier"]?.value.multiplierAsDecimal()
                        BigDecimal(amount) * modifier
                    } else BigDecimal.ZERO
                }

        val bounty = bountyRegex.find(messageMetadata.text.trimIndent())
                .let { match ->
                    if (match != null) {
                        val amount = match.groups["amount"]?.value
                        val modifier = match.groups["multiplier"]?.value.multiplierAsDecimal()
                        BigDecimal(amount) * modifier
                    } else null
                }

        return when (type) {
            GameType.TOURNAMENT ->
                TournamentGame(
                        id = UUID.randomUUID(),
                        buyIn = buyin.defaultScale(),
                        stack = stack.defaultScale(),
                        players = emptyList(),
                        createdAt = messageMetadata.createdAt,
                )

            GameType.CASH ->
                CashGame(
                        id = UUID.randomUUID(),
                        buyIn = buyin.defaultScale(),
                        stack = stack.defaultScale(),
                        players = emptyList(),
                        createdAt = messageMetadata.createdAt,
                )

            GameType.BOUNTY -> {
                check(bounty != null) { "Bounty should be specified" }
                BountyTournamentGame(
                        id = UUID.randomUUID(),
                        buyIn = buyin.defaultScale(),
                        stack = stack.defaultScale(),
                        bounty = bounty.defaultScale(),
                        players = emptyList(),
                        createdAt = messageMetadata.createdAt,
                )
            }
        }
    }

    private fun String?.multiplierAsDecimal(): BigDecimal {
        return when (this?.uppercase()) {
            "H" -> BigDecimal("100")
            "K" -> BigDecimal("1000")
            "M" -> BigDecimal("1000000")
            else -> BigDecimal.ONE
        }
    }

    private fun BigDecimal.defaultScale(): BigDecimal = this.setScale(0, RoundingMode.HALF_DOWN)

}