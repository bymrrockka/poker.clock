package by.mrrockka.commands

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.BountyTournamentSummary
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.CashSummary
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.PrizeGameSummary
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.TournamentSummary
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.domain.toSummary
import by.mrrockka.domain.toTournamentSummary
import by.mrrockka.domain.totalEntries
import by.mrrockka.repo.PinType
import by.mrrockka.service.CalculationTelegramService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.stereotype.Component
import java.math.BigDecimal.ZERO

interface CalculationCommandHandler {
    suspend fun calculate(message: MessageUpdate)
}

@Component
class CalculationCommandHandlerImpl(
        private val bot: TelegramBot,
        private val gameService: GameTelegramService,
        private val pinMessageService: PinMessageService,
        private val calculationService: CalculationTelegramService,
) : CalculationCommandHandler {

    private val buyMeACoffee = """
        |${"-".repeat(30)}
        |You can support me using this link. 
        |https://buymeacoffee.com/mrrockka
        |
        """.trimMargin()

    @CommandHandler(["/calculate"])
    override suspend fun calculate(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        val game = gameService.findGame(metadata)
        calculationService.calculate(metadata)
                .let { payouts ->
                    message { payouts.response(game) }
                            .sendReturning(to = metadata.chatId, via = bot)
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
                val summaries = game.toSummary().map { it as CashSummary }.associateBy { it.person }
                buyMeACoffee + joinToString(separator = "\n") {
                    val summary = summaries[it.creditor]
                            ?: error("No game summary for ${it.creditor}")
                    """
                    |${"-".repeat(30)}
                    |Payout to: @${summary.person.nickname}
                    |  Entries: ${summary.entries().setScale(0)}
                    |  Withdrawals: ${summary.withdrawals.setScale(0)}
                    |  Total: ${it.total.setScale(0)} 
                    |${it.debtors.message()}
                    """.trimMargin() + equalResponse()
                }
            }

            is TournamentGame -> {
                val summaries = game.toTournamentSummary().associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val summary = (summaries[it.creditor]
                                    ?: error("No game summary for ${it.creditor}")) as TournamentSummary
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${summary.person.nickname}
                            |  Entries: ${summary.entries().setScale(0)}
                            |  Total: ${it.total.setScale(0)} (won ${summary.prize.setScale(0)} - entries ${summary.entries().setScale(0)})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                return buyMeACoffee + game.finalePlacesMessage() + payoutsResponse + equalResponse()
            }

            is BountyTournamentGame -> {
                val summaries = game.toTournamentSummary().associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val summary = (summaries[it.creditor]
                                    ?: error("No game summary for ${it.creditor}")) as BountyTournamentSummary
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${summary.person.nickname}
                            |  Entries: ${summary.entries().setScale(0)}
                            |  Bounties: ${summary.bounty.total.setScale(0)} (taken ${summary.bounty.taken.setScale(0)} - given ${summary.bounty.given.setScale(0)}) 
                            |  Total: ${it.total.setScale(0)} (won ${summary.prize.setScale(0)} - entries ${summary.entries().setScale(0)} ${if (summary.bounty.total < ZERO) "-" else "+"} bounties ${summary.bounty.total.setScale(0)})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                return buyMeACoffee + game.finalePlacesMessage() + payoutsResponse + this.equalResponse()
            }

            else -> error("Unknown game type")
        }
    }

    private fun List<Payout>.equalResponse(): String {
        val zeros = filter { it.total == ZERO }
        return if (zeros.isNotEmpty()) """
            |
            |${"-".repeat(30)}
            |Players played equally
            ${zeros.joinToString("\n") { "|  @${it.creditor.nickname}" }}
        """.trimMargin() else ""
    }

    private fun List<Debtor>.message(): String {
        if (isEmpty()) return ""

        return """
            |From:
            ${joinToString("\n") { "|  @${it.person.nickname} -> ${it.debt.setScale(0)}" }}
        """.trimMargin()
    }

    private fun Game.finalePlacesMessage(): String {
        val summary = toTournamentSummary().sortedBy { it.position }
        return """
                |${"-".repeat(30)}
                |Finale summary:
                ${summary.filter { it.position != null }.joinToString("\n") { "|  ${it.position}. @${it.person.nickname} won ${it.prize.setScale(0)}" }}
                |Total: ${players.totalEntries().setScale(0)} (${players.flatMap { it.entries }.size} entries * ${buyIn.setScale(0)} buy in)
                |
                """.trimMargin()
    }

    private fun List<Payout>.prepare(summaries: Map<Person, PrizeGameSummary>): List<Payout> = filter { it.total > ZERO }
            .sortedBy { summaries[it.creditor]?.position }
            .reversed()
}