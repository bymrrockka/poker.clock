package by.mrrockka.parser

import by.mrrockka.BotCommands
import by.mrrockka.domain.Game
import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.MetadataEntity
import by.mrrockka.domain.game
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class GameMessageParser(
        private val commands: BotCommands,
) : MessageParser<Game> {
    private val amountRegex = "(?<amount>[.\\d]+)(?<multiplier>[A-z]{0,1})"
    private val buyInRegex = "^buyin:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val stackRegex = "^stack:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val bountyRegex = "^bounty:\\s*$amountRegex".toRegex(RegexOption.MULTILINE)
    private val typeRegex = "([\\w]+)_game$".toRegex(RegexOption.MULTILINE)

    override fun parse(metadata: MessageMetadata): Game {
        val type = metadata.command.toType()
        val buyin = buyInRegex.find(metadata.text.trimIndent())
                .let { match ->
                    val amount = match?.groups["amount"]?.value
                    val modifier = match?.groups["multiplier"]?.value.multiplierAsDecimal()
                    check(amount != null) { "Buy in should be specified" }
                    BigDecimal(amount) * modifier
                }

        val stack = stackRegex.find(metadata.text.trimIndent())
                .let { match ->
                    if (match != null) {
                        val amount = match.groups["amount"]?.value
                        val modifier = match.groups["multiplier"]?.value.multiplierAsDecimal()
                        BigDecimal(amount) * modifier
                    } else BigDecimal.ZERO
                }

        val bounty = bountyRegex.find(metadata.text.trimIndent())
                .let { match ->
                    if (match != null) {
                        val amount = match.groups["amount"]?.value
                        val modifier = match.groups["multiplier"]?.value.multiplierAsDecimal()
                        BigDecimal(amount) * modifier
                    } else null
                }

        return game(type, buyin, stack, bounty, metadata.createdAt)
    }

    private fun String?.multiplierAsDecimal(): BigDecimal {
        return when (this?.uppercase()) {
            "H" -> BigDecimal("100")
            "K" -> BigDecimal("1000")
            "M" -> BigDecimal("1000000")
            else -> BigDecimal.ONE
        }
    }


    private fun MetadataEntity.toType(): GameType {
        val description = commands.byNameAndAlias[text]
        check(description != null) { "Can't find game by command" }

        return typeRegex.find(description.name.trimIndent())!!.destructured
                .let { (match) -> GameType.valueOf(match.uppercase()) }
    }
}