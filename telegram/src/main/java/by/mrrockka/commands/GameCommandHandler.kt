package by.mrrockka.commands

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.Game
import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.game
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.GameTablesService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.WizardHandler
import eu.vendeli.tgbot.api.message.deleteMessages
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.generated.getState
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.UserChatReference
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStateManager
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.reflect.KClass

interface GameCommandHandler {
    suspend fun store(message: MessageUpdate)
}

@Component
class GameCommandHandlerImpl(
        private val bot: TelegramBot,
        private val gameService: GameTelegramService,
        private val pinMessageService: PinMessageService,
        private val tablesService: GameTablesService,
) : GameCommandHandler {

    @CommandHandler(["/tournament_game", "/bounty_game", "/cash_game", "/tg", "/bg", "/cg"])
    @Deprecated(message = "This functionality will be replaced with step by step game conversation", replaceWith = ReplaceWith("/game", "GameWizardHandler"))
    override suspend fun store(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        gameService.store(metadata)
                .let { game ->
                    """
                    |${
                        when (game) {
                            is CashGame -> "Cash game started."
                            is TournamentGame -> "Tournament game started."
                            is BountyTournamentGame -> "Bounty tournament game started."
                            else -> error("Game type not supported: ${this.javaClass.simpleName}")
                        }
                    }
                    ${
                        tablesService.generate(game)
                                .joinToString("\n") { table ->
                                    """|${"-".repeat(30)}
                                    |Table ${table.id}
                                    |Seats:
                                    ${table.seats.sortedBy { it.num }.joinToString("\n") { seat -> "|  ${seat.num}. @${seat.nickname}" }}
                                """
                                }
                    }
                    """.trimMargin()
                }.let { response ->
                    message { response }
                            .sendReturning(to = metadata.chatId, via = bot)
                            .onFailure { error("Failed to send game message") }
                            ?: error("No message returned from telegram api")
                }.also { message -> pinMessageService.pin(message, PinType.GAME) }
    }

}


class MapBigDecimalStateManager : WizardStateManager<BigDecimal> {
    val state = mutableMapOf<KClass<out WizardStep>, BigDecimal>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): BigDecimal? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: BigDecimal) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

class MapGameTypeStateManager : WizardStateManager<GameType> {
    val state = mutableMapOf<KClass<out WizardStep>, GameType>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): GameType? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: GameType) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

class MapMetadataStateManager : WizardStateManager<MessageMetadata> {
    val state = mutableMapOf<KClass<out WizardStep>, MessageMetadata>()
    override suspend fun get(key: KClass<out WizardStep>, reference: UserChatReference): MessageMetadata? = state[key]

    override suspend fun set(key: KClass<out WizardStep>, reference: UserChatReference, value: MessageMetadata) {
        state[key] = value
    }

    override suspend fun del(key: KClass<out WizardStep>, reference: UserChatReference) {
        state.remove(key)
    }
}

@WizardHandler(
        trigger = ["/game", "/start"],
        stateManagers = [MapBigDecimalStateManager::class, MapMetadataStateManager::class],
)
object GameWizardHandler {
    lateinit var gameService: GameTelegramService
    lateinit var tableService: GameTablesService
    lateinit var pinMessageService: PinMessageService
    val messages = mutableListOf<Long>()
    lateinit var initial: MessageMetadata
    var lastGame: Game? = null

    private val decimalValidation = { ctx: WizardContext -> ctx.update.text.matches("^([\\d.]+)$".toRegex()) }
    private fun messagesForDeletion(messageId: Long) {
        messages += messageId
    }

    @WizardHandler.StateManager(MapGameTypeStateManager::class)
    object Type : WizardStep(isInitial = true) {
        override suspend fun onEntry(ctx: WizardContext) {
            initial = ctx.update.toMessageMetadata()
            lastGame = gameService.findLastGame(initial)
            pinMessageService.pin(ctx.update.origin.message!!, PinType.GAME)

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
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Type should be one of ${GameType.entries.joinToString { it.name.lowercase() }}" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun store(ctx: WizardContext): GameType {
            return GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) }
                    ?: error("Type ${ctx.update.text} not supported")
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            messagesForDeletion(ctx.update.origin.message!!.messageId)
            return if (GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) } != null) {
                Transition.Next
            } else {
                Transition.Retry
            }
        }
    }

    object Buyin : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "How much is for buy in?" }
                    .replyKeyboardMarkup {
                        if (lastGame != null) {
                            +lastGame!!.buyIn.setScale(0).toString()
                            options {
                                resizeKeyboard = true
                                oneTimeKeyboard = true
                            }
                        }
                    }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Buy in is necessary for calculations and it should be a number" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun store(ctx: WizardContext): BigDecimal {
            return BigDecimal(ctx.update.text)
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            messagesForDeletion(ctx.update.origin.message!!.messageId)
            return when {
                !decimalValidation(ctx) -> return Transition.Retry
                ctx.getState<Type>() == GameType.BOUNTY -> Transition.JumpTo(Bounty::class)
                else -> return Transition.JumpTo(Players::class)
            }
        }
    }

    object Bounty : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "How much is for bounty?" }
                    .replyKeyboardMarkup {
                        if (lastGame is BountyTournamentGame) {
                            +(lastGame as BountyTournamentGame).bounty.setScale(0).toString()
                            options {
                                resizeKeyboard = true
                                oneTimeKeyboard = true
                            }
                        }
                    }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Bounty is necessary for Bounty tournament and it should be a number" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun store(ctx: WizardContext): BigDecimal {
            return BigDecimal(ctx.update.text)
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            messagesForDeletion(ctx.update.origin.message!!.messageId)
            if (decimalValidation(ctx)) {
                return Transition.Next
            } else {
                return Transition.Retry
            }
        }
    }

    object Players : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "Who's playing?" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Players mentions required to start a game. Like @mention" }
                    .sendReturning(ctx.user, ctx.bot)
                    .onFailure { error("Failed to send message") }
                    .also { message -> messagesForDeletion(message!!.messageId) }
        }

        override suspend fun store(ctx: WizardContext): MessageMetadata {
            return ctx.update.origin.message!!.toMessageMetadata()
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            messagesForDeletion(ctx.update.origin.message!!.messageId)
            val message = ctx.update.toMessageMetadata()
            return when {
                message.mentions.isNotEmpty() || message.replyTo?.poll != null -> Transition.Next
                else -> Transition.Retry
            }
        }

    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
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
                                    ${table.seats.sortedBy { it.num }.joinToString("\n") { seat -> "|  ${seat.num}. @${seat.nickname}" }}
                                """
                            }
                }
                """.trimMargin()
            }.let { response ->
                message { response }
                        .sendReturning(to = ctx.user, via = ctx.bot)
                        .onFailure { error("Failed to send game message") }
                        ?: error("No message returned from telegram api")
            }

            deleteMessages(messages).send(ctx.user, ctx.bot)
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return Transition.Finish
        }
    }
}