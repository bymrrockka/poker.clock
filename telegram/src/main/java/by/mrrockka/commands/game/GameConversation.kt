package by.mrrockka.commands.game

import by.mrrockka.commands.BigDecimalState
import by.mrrockka.commands.CancelStep
import by.mrrockka.commands.CancelableStep
import by.mrrockka.commands.GameTypeState
import by.mrrockka.commands.MessageLogConversation
import by.mrrockka.commands.MessageMetadataState
import by.mrrockka.commands.decimalValidation
import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.game
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.GameTablesService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.annotations.WizardHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.generated.getState
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.onFailure
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal

@WizardHandler(
        trigger = ["/game", "/start"],
        stateManagers = [BigDecimalState::class, MessageMetadataState::class, GameTypeState::class],
)
object GameConversation : MessageLogConversation() {
    lateinit var gameService: GameTelegramService
    lateinit var tableService: GameTablesService
    lateinit var pinMessageService: PinMessageService

    object Type : CancelableStep(isInitial = true, cancelStep = Cancel::class) {
        override suspend fun onEntry(ctx: WizardContext) {
            ctx.cancelableMessage { message -> ctx.user.id.message(message.messageId) }
            ctx.initialize()

            message { "What type of game you'd like to play?" }
                    .replyKeyboardMarkup {
                        GameType.entries.map { type ->
                            +type.title
                        }
                        options {
                            resizeKeyboard = true
                            oneTimeKeyboard = true
                        }
                    }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Type should be one of ${GameType.entries.joinToString { it.name.lowercase() }}" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun store(ctx: WizardContext): GameType =
                GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) }
                        ?: error("No game type found for ${ctx.update.text}")

        override suspend fun navigate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                (GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) } != null) ->
                    Transition.Next

                else -> Transition.Retry()
            }
        }
    }

    object Buyin : CancelableStep(cancelStep = Cancel::class) {
        override suspend fun onEntry(ctx: WizardContext) {
            val lastGame = ctx.user.id.initial().let { gameService.findLastGame(it) }
            message { "How much is for buy in?" }
                    .replyKeyboardMarkup {
                        if (lastGame != null) {
                            +lastGame.buyIn.setScale(0).toString()
                            options {
                                resizeKeyboard = true
                                oneTimeKeyboard = true
                            }
                        }
                    }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Buy in is necessary for calculations and it should be a number" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun store(ctx: WizardContext): BigDecimal = BigDecimal(ctx.update.text)

        override suspend fun navigate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                !ctx.update.text.decimalValidation() -> Transition.Retry()
                ctx.getState<Type>() == GameType.BOUNTY -> Transition.JumpTo(Bounty::class)
                else -> Transition.JumpTo(Players::class)
            }
        }
    }

    object Players : CancelableStep(cancelStep = Cancel::class) {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "Who's playing?" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Players mentions required to start a game. Like @mention" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun store(ctx: WizardContext): MessageMetadata =
                ctx.update.origin.message!!.toMessageMetadata()

        override suspend fun navigate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            val message = ctx.update.toMessageMetadata()
            return when {
                message.mentions.isNotEmpty() || message.replyTo?.poll != null -> Transition.Next
                else -> Transition.Retry()
            }
        }

    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val type = ctx.getState<Type>() ?: error("Type is null")
            val buyin = ctx.getState<Buyin>() ?: error("Buyin is null")
            val bounty = ctx.getState<Bounty>()
            val playersMessage = ctx.getState<Players>() ?: error("Players message is null")

            transaction {
                gameService.store(
                        game(
                                type = type,
                                bounty = bounty,
                                buyin = buyin,
                                createdAt = ctx.user.id.initial().createdAt,
                        ),
                        initial = ctx.user.id.initial(),
                        players = playersMessage,
                ).let { game ->
                    """
                |Game type: $type
                |Buy in: $buyin
                ${if (type == GameType.BOUNTY) "|Bounty $bounty" else ""}
                ${
                        tableService.generate(game)
                                .joinToString("\n") { table ->
                                    """|${"-".repeat(30)}
                                    |Table ${table.id}
                                    |Seats:
                                    ${
                                        table.seats.sortedBy { it.num }
                                                .joinToString("\n") { seat -> "|  ${seat.num}. @${seat.nickname}" }
                                    }
                                """
                                }
                    }
                """.trimMargin()
                }
            }.let { response ->
                message { response }
                        .sendReturning(to = ctx.user, via = ctx.bot)
                        .onFailure { error("Failed to send game message") }
                        ?.also { message -> pinMessageService.pin(message, PinType.GAME) }
            }

            ctx.clearMessages()
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return Transition.Finish
        }
    }

    object Bounty : CancelableStep(cancelStep = Cancel::class) {
        override suspend fun onEntry(ctx: WizardContext) {
            val lastGame = gameService.findLastGame(ctx.user.id.initial())
            message { "How much is for bounty?" }
                    .replyKeyboardMarkup {
                        if (lastGame is BountyTournamentGame) {
                            +lastGame.bounty.setScale(0).toString()
                            options {
                                resizeKeyboard = true
                                oneTimeKeyboard = true
                            }
                        }
                    }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Bounty is necessary for Bounty tournament and it should be a number" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    ?.also { message -> ctx.user.id.message(message.messageId) }
        }

        override suspend fun store(ctx: WizardContext): BigDecimal = BigDecimal(ctx.update.text)

        override suspend fun navigate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                ctx.update.text.decimalValidation() -> Transition.JumpTo(Players::class)
                else -> Transition.Retry()
            }
        }
    }

    object Cancel : CancelStep({ ctx -> ctx.clearMessages() })
}
