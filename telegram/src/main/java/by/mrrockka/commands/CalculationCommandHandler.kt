package by.mrrockka.commands

import by.mrrockka.domain.BasicPerson
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.ServiceFee
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.domain.totalEntries
import by.mrrockka.feature.ServiceFeeFeature
import by.mrrockka.repo.PinType
import by.mrrockka.service.BountyTournamentPlayerSummary
import by.mrrockka.service.CalculationTelegramService
import by.mrrockka.service.CashPlayerSummary
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import by.mrrockka.service.PlayerPrizeSummary
import by.mrrockka.service.PlayerSummary
import by.mrrockka.service.PlayerSummaryService
import by.mrrockka.service.TournamentPlayerSummary
import by.mrrockka.service.up
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.Guard
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ZERO

interface CalculationCommandHandler {
    suspend fun calculate(message: MessageUpdate)
}

@Component
@Transactional(propagation = Propagation.REQUIRED)
open class CalculationCommandHandlerImpl(
        private val bot: TelegramBot,
        private val gameService: GameTelegramService,
        private val pinMessageService: PinMessageService,
        private val calculationService: CalculationTelegramService,
        private val playerSummaryService: PlayerSummaryService,
        private val serviceFeeFeature: ServiceFeeFeature,
) : CalculationCommandHandler {

    private val buyMeACoffee = """
        |
        |${"-".repeat(30)}
        |You can support me using this link. 
        |${serviceFeeFeature.url}
        """.trimMargin()

    @CommandHandler(["/calculate"])
    @Guard(ExcludeBotGuard::class)
    override suspend fun calculate(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        val game = gameService.findGame(metadata)
        calculationService.calculate(metadata)
                .let { payouts ->
                    message {
                        payouts.filter { it.creditor !is ServiceFee }
                                .response(game) + payouts.serviceFeeText()
                    }.sendReturning(to = metadata.chatId, via = bot)
                            .onFailure { error("Failed to send payouts message") }
                            ?: error("No message returned from telegram api")
                }.also { message ->
                    pinMessageService.pin(message)
                    pinMessageService.unpinAll(message, PinType.GAME)
                }
    }

    private fun List<Payout>.response(game: Game): String {
        return when (game) {
            is CashGame -> {
                val summaries = playerSummaryService.summary(game).map { it as CashPlayerSummary }.associateBy { it.person }
                joinToString(separator = "\n") {
                    val summary = summaries[it.creditor]
                            ?: error("No game summary for ${it.creditor}")
                    """
                    |${"-".repeat(30)}
                    |Payout to: @${summary.person.nickname}
                    |  Entries: ${summary.entries}
                    |  Withdrawals: ${summary.withdrawals.up()}
                    |  Total: ${it.total} (${summary.totalText()}${it.feeText()})
                    |${it.debtors.message()}
                    """.trimMargin()
                }
            }

            is TournamentGame -> {
                val summaries = playerSummaryService.tournamentSummary(game).associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val summary = (summaries[it.creditor]
                                    ?: error("No game summary for ${it.creditor}")) as TournamentPlayerSummary
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${summary.person.nickname}
                            |  Entries: ${summary.entries}
                            |  Total: ${it.amount.up()} (${summary.totalText()}${it.feeText()})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                game.finalePlacesMessage() + payoutsResponse
            }

            is BountyTournamentGame -> {
                val summaries = playerSummaryService.tournamentSummary(game).associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val summary = (summaries[it.creditor]
                                    ?: error("No game summary for ${it.creditor}")) as BountyTournamentPlayerSummary
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${summary.person.nickname}
                            |  Entries: ${summary.entries}
                            |  Bounties: ${summary.bounty.total.up()} (taken ${summary.bounty.taken} - given ${summary.bounty.given}) 
                            |  Total: ${it.total} (${summary.totalText()}${it.feeText()})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                game.finalePlacesMessage() + payoutsResponse
            }

            else -> error("Unknown game type")
        } + equalResponse()
    }

    private fun List<Payout>.equalResponse(): String {
        val zeros = filter { it.amount == ZERO }
        return if (zeros.isNotEmpty()) """
            |
            |${"-".repeat(30)}
            |Players played equally
            ${
            zeros.filter { it.creditor !is ServiceFee }.joinToString("\n") {
                when (val person = it.creditor) {
                    is BasicPerson -> "|  @${person.nickname}"
                    else -> error("Unknown person")
                }
            }
        }
        """.trimMargin() else ""
    }

    private fun List<Debtor>.message(): String {
        if (isEmpty()) return ""

        return """
            |From:
            ${
            joinToString("\n") {
                when (val person = it.person) {
                    is BasicPerson -> "|  @${person.nickname} -> ${it.debt}"
                    else -> error("Unknown person")
                }
            }
        }
        """.trimMargin()
    }

    private fun Game.finalePlacesMessage(): String {
        val summary = playerSummaryService.tournamentSummary(this).sortedBy { it.position }
        return """
                |${"-".repeat(30)}
                |Finale summary:
                ${summary.filter { it.position != null }.joinToString("\n") { "|  ${it.position}. @${it.person.nickname} won ${it.prize.up()}" }}
                |Total: ${players.totalEntries().up()} (${players.flatMap { it.entries }.size} entries * ${buyIn.up()} buy in)
                |
                """.trimMargin()
    }

    private fun List<Payout>.prepare(summaries: Map<out Person, PlayerPrizeSummary>): List<Payout> = filter { it.amount > ZERO }
            .sortedBy { summaries[it.creditor]?.position }
            .reversed()

    private fun List<Payout>.serviceFeeIsEqual(): Boolean = any { it.creditor is ServiceFee && it.amount.up() == ZERO }

    private fun List<Payout>.serviceFeeText(): String {
        return if (serviceFeeFeature.enabled && !serviceFeeIsEqual()) {
            find { it.creditor is ServiceFee }
                    ?.let { fee ->
                        """
                            |
                            |${"-".repeat(30)}
                            |${serviceFeeFeature.description}
                            |  URL: ${serviceFeeFeature.url}
                            |  Total: ${fee.amount.up()}
                            |${fee.debtors.message()}
                        """.trimMargin()
                    } ?: buyMeACoffee
        } else buyMeACoffee
    }

    private fun PlayerSummary.totalText(): String = when (this) {
        is BountyTournamentPlayerSummary -> "won ${prize.up()} - entries ${entries} ${if (bounty.total < ZERO) "-" else "+"} bounties ${bounty.total.up()}"
        is TournamentPlayerSummary -> "won ${prize.up()} - entries ${entries}"
        is CashPlayerSummary -> "withdrawals ${withdrawals.up()} - entries ${entries}"
        else -> error("Unknown game summary ${this::class.simpleName}")
    }

    private fun Payout.feeText(): String = if (fee.up() != ZERO) {
        " - ${fee.up()} fee"
    } else ""

}