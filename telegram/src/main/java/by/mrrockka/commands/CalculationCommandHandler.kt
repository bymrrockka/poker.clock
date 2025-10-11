package by.mrrockka.commands

import by.mrrockka.domain.BountyPlayer
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.CashPlayer
import by.mrrockka.domain.Debtor
import by.mrrockka.domain.Game
import by.mrrockka.domain.Payout
import by.mrrockka.domain.Person
import by.mrrockka.domain.PrizeSummary
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.TournamentPlayer
import by.mrrockka.domain.takenToGiven
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.domain.toSummary
import by.mrrockka.domain.total
import by.mrrockka.domain.totalEntries
import by.mrrockka.repo.PinType
import by.mrrockka.service.CalculationTelegramService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.math.BigDecimal.ZERO

private val logger = KotlinLogging.logger {}

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

    @CommandHandler(["/calculate"])
    override suspend fun calculate(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        val game = gameService.findGame(metadata)
        calculationService.calculate(metadata)
                .let { payouts ->
                    sendMessage { payouts.response(game) }
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
            is CashGame -> joinToString(separator = "\n") {
                val player = it.creditor as CashPlayer
                """
                |${"-".repeat(30)}
                |Payout to: @${player.person.nickname}
                |  Entries: ${player.entries.total().setScale(0)}
                |  Withdrawals: ${player.withdrawals.total().setScale(0)}
                |  Total: ${it.total.setScale(0)} 
                |${it.debtors.message()}
                """.trimMargin() + equalResponse()
            }

            is TournamentGame -> {
                val summaries = game.toSummary().associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val player = it.creditor as TournamentPlayer
                            val prize = summaries[it.creditor.person]?.amount
                                    ?: error("No prize for ${it.creditor.person}")
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${player.person.nickname}
                            |  Entries: ${player.entries.size}
                            |  Total: ${it.total.setScale(0)} (won ${prize.setScale(0)} - entries ${player.entries.total().setScale(0)})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                return game.finalePlacesMessage() + payoutsResponse + equalResponse()
            }

            is BountyTournamentGame -> {
                val summaries = game.toSummary().associateBy { it.person }
                val payoutsResponse = prepare(summaries)
                        .joinToString(separator = "\n") {
                            val player = it.creditor as BountyPlayer
                            val prize = summaries[it.creditor.person]?.amount
                                    ?: error("No prize for ${it.creditor.person}")
                            val (taken, given) = player.takenToGiven()
                            val bountiesTotal = taken.total() - given.total()
                            """
                            |${"-".repeat(30)}
                            |Payout to: @${player.person.nickname}
                            |  Entries: ${player.entries.size}
                            |  Bounties: ${bountiesTotal.setScale(0)} (taken ${taken.size} - given ${given.size}) 
                            |  Total: ${it.total.setScale(0)} (won ${prize.setScale(0)} - entries ${player.entries.total().setScale(0)} ${if (bountiesTotal < ZERO) "-" else "+"} bounties ${bountiesTotal.setScale(0)})
                            |${it.debtors.message()}
                            """.trimMargin()
                        }

                return game.finalePlacesMessage() + payoutsResponse + this.equalResponse()
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
            ${zeros.joinToString("\n") { "|  @${it.creditor.person.nickname}" }}
        """.trimMargin() else ""
    }

    private fun List<Debtor>.message(): String {
        if (isEmpty()) return ""

        return """
            |From:
            ${joinToString("\n") { "|  @${it.player.person.nickname} -> ${it.debt.setScale(0)}" }}
        """.trimMargin()
    }

    private fun Game.finalePlacesMessage(): String {
        val summary = toSummary()
        return """
                |${"-".repeat(30)}
                |Finale summary:
                ${summary.joinToString("\n") { "|  ${it.position}. @${it.person.nickname} won ${it.amount.setScale(0)}" }}
                |Total: ${players.totalEntries().setScale(0)} (${players.flatMap { it.entries }.size} entries * ${buyIn.setScale(0)} buy in)
                |
                """.trimMargin()
    }

    private fun List<Payout>.prepare(summaries: Map<Person, PrizeSummary>): List<Payout> = filter { it.total > ZERO }
            .sortedBy { summaries[it.creditor.person]?.position }
            .reversed()

}