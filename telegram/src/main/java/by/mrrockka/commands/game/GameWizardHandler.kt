package by.mrrockka.commands.game

import by.mrrockka.commands.BigDecimalState
import by.mrrockka.commands.GameTypeState
import by.mrrockka.commands.MessageMetadataState
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
import eu.vendeli.tgbot.api.message.deleteMessages
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.generated.getState
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.onFailure
import java.math.BigDecimal

@WizardHandler(
    trigger = ["/game", "/start"],
    stateManagers = [BigDecimalState::class, MessageMetadataState::class, GameTypeState::class],
)
object GameWizardHandler {
    lateinit var gameService: GameTelegramService
    lateinit var tableService: GameTablesService
    lateinit var pinMessageService: PinMessageService
    val messages = mutableMapOf<Long, List<Long>>()
    val initials = mutableMapOf<Long, MessageMetadata>()

    private fun String.decimalValidation() = matches("^([\\d.]+)$".toRegex())

    private fun Long.message(messageId: Long) {
        val list = messages[this]
        if (list != null) {
            messages[this] = (list + messageId)
        } else {
            messages[this] = mutableListOf(messageId)
        }
    }

    private fun String.canceled() = matches("^cancel$".toRegex())

    private fun jumpToCancel(): Transition = Transition.JumpTo(Cancel::class)

    object Type : WizardStep(isInitial = true) {
        override suspend fun onEntry(ctx: WizardContext) {
            val metadata = ctx.update.toMessageMetadata()
            initials += ctx.user.id to metadata

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
                ?: error("No message returned from telegram api")
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Type should be one of ${GameType.entries.joinToString { it.name.lowercase() }}" }
                .sendReturning(ctx.user, ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
                ?: error("No message returned from telegram api")
        }

        override suspend fun store(ctx: WizardContext): GameType? {
            return when {
                ctx.update.text.canceled() -> null
                else -> GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) }
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                ctx.update.text.canceled() -> jumpToCancel()
                (GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) } != null) ->
                    Transition.Next

                else -> Transition.Retry()
            }
        }
    }

    object Buyin : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val lastGame = initials[ctx.user.id]?.let { gameService.findLastGame(it) }
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
                ?: error("No message returned from telegram api")
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Buy in is necessary for calculations and it should be a number" }
                .sendReturning(ctx.user, ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
                ?: error("No message returned from telegram api")
        }

        override suspend fun store(ctx: WizardContext): BigDecimal? {
            return when {
                ctx.update.text.canceled() -> null
                else -> BigDecimal(ctx.update.text)
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                ctx.update.text.canceled() -> jumpToCancel()
                !ctx.update.text.decimalValidation() -> Transition.Retry()
                ctx.getState<Type>() == GameType.BOUNTY -> Transition.JumpTo(Bounty::class)
                else -> Transition.JumpTo(Players::class)
            }
        }
    }

    object Players : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "Who's playing?" }
                .sendReturning(ctx.user, ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
                ?: error("No message returned from telegram api")
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Players mentions required to start a game. Like @mention" }
                .sendReturning(ctx.user, ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
                ?: error("No message returned from telegram api")
        }

        override suspend fun store(ctx: WizardContext): MessageMetadata? {
            return when {
                ctx.update.text.canceled() -> null
                else -> ctx.update.origin.message?.toMessageMetadata()
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            val message = ctx.update.toMessageMetadata()
            return when {
                ctx.update.text.canceled() -> jumpToCancel()
                message.mentions.isNotEmpty() || message.replyTo?.poll != null -> Transition.Next
                else -> Transition.Retry()
            }
        }

    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val initial = initials[ctx.user.id] ?: error("Can't find initial user message")
            val type = ctx.getState<Type>() ?: error("Type is null")
            val buyin = ctx.getState<Buyin>() ?: error("Buyin is null")
            val bounty = ctx.getState<Bounty>()
            val playersMessage = ctx.getState<Players>() ?: error("Players message is null")

            gameService.store(
                game(
                    type = type,
                    bounty = bounty,
                    buyin = buyin,
                    createdAt = initial.createdAt,
                ),
                initial = initial,
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
            }.let { response ->
                message { response }
                    .sendReturning(to = ctx.user, via = ctx.bot)
                    .onFailure { error("Failed to send game message") }
                    ?.also { message -> pinMessageService.pin(message, PinType.GAME) }
                    ?: error("No message returned from telegram api")
            }

            messages[ctx.user.id]?.also { list ->
                deleteMessages(list).send(ctx.user, ctx.bot)
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return Transition.Finish
        }
    }

    object Bounty : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val lastGame = initials[ctx.user.id]?.let { gameService.findLastGame(it) }
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
                ?: error("No message returned from telegram api")
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) {
            message { "Bounty is necessary for Bounty tournament and it should be a number" }
                .sendReturning(ctx.user, ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
                ?: error("No message returned from telegram api")
        }

        override suspend fun store(ctx: WizardContext): BigDecimal? {
            return when {
                ctx.update.text.canceled() -> null
                else -> BigDecimal(ctx.update.text)
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            ctx.user.id.message(ctx.update.origin.message!!.messageId)
            return when {
                ctx.update.text.canceled() -> jumpToCancel()
                ctx.update.text.decimalValidation() -> Transition.JumpTo(Players::class)
                else -> Transition.Retry()
            }
        }
    }

    object Cancel : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "Game creation was cancelled" }.send(ctx.user, ctx.bot)

            messages[ctx.user.id]?.also { list ->
                deleteMessages(list).send(ctx.user, ctx.bot)
            }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return Transition.Finish
        }
    }
}
