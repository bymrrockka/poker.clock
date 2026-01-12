package by.mrrockka.commands

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.GameType
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.GameTablesService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.WizardHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.generated.getState
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.UserChatReference
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStateManager
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.onFailure
import org.springframework.beans.factory.annotation.Autowired
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
                    sendMessage { response }
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

@Component
@WizardHandler(
        trigger = ["/game", "/start"],
        stateManagers = [MapBigDecimalStateManager::class],
)
object GameWizardHandler {
    @Autowired
    var gameService: GameTelegramService? = null
    val decimalValidation = { ctx: WizardContext -> ctx.update.text.matches("^([\\d]+)$".toRegex()) }

    @WizardHandler.StateManager(MapGameTypeStateManager::class)
    object Type : WizardStep(isInitial = true) {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "What type of game you'd like to play?" }.send(ctx.user, ctx.bot)
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Type should be one of ${GameType.entries.joinToString { it.name.lowercase() }}" }.send(ctx.user, ctx.bot)
        }

        override suspend fun store(ctx: WizardContext): GameType {
            return GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) }
                    ?: error("Type ${ctx.update.text} not supported")
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return if (GameType.entries.find { it.name.equals(ctx.update.text, ignoreCase = true) } != null) {
                Transition.Next
            } else {
                Transition.Retry
            }
        }
    }

    object Buyin : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "How much is for buy in?" }.send(ctx.user, ctx.bot)
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Buy in is necessary for calculations and it should be a number" }.send(ctx.user, ctx.bot)
        }

        override suspend fun store(ctx: WizardContext): BigDecimal {
            return BigDecimal(ctx.update.text)
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return when {
                !decimalValidation(ctx) -> return Transition.Retry
                ctx.getState<Type>() == GameType.BOUNTY -> Transition.JumpTo(Bounty::class)
                else -> return Transition.JumpTo(Finish::class)
            }
        }
    }

    object Bounty : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            message { "How much is for bounty?" }.send(ctx.user, ctx.bot)
        }

        override suspend fun onRetry(ctx: WizardContext) {
            message { "Bounty is necessary for Bounty tournament and it should be a number" }.send(ctx.user, ctx.bot)
        }

        override suspend fun store(ctx: WizardContext): BigDecimal {
            return BigDecimal(ctx.update.text)
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            if (decimalValidation(ctx)) {
                return Transition.Next
            } else {
                return Transition.Retry
            }
        }
    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val type = ctx.getState<Type>() ?: error("Type is null")
            val buyin = ctx.getState<Buyin>() ?: error("Buyin is null")
            val bounty = ctx.getState<Bounty>()

            message {
                """
                |Next properties specified for game
                |Game type: $type
                |Buy in: $buyin
                ${if (type == GameType.BOUNTY) "|Bounty $bounty" else ""}
            """.trimMargin().trimIndent()
            }

            val metadata = ctx.update.origin.message?.toMessageMetadata() ?: error("Unknown message")
            gameService?.store(metadata)
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
                    """.trimMargin()
                    }.let { response ->
                        sendMessage { response }
                                .sendReturning(to = metadata.chatId, via =ctx.bot)
                                .onFailure { error("Failed to send game message") }
                                ?: error("No message returned from telegram api")
                    }
        }

        override suspend fun validate(ctx: WizardContext): Transition {
            return Transition.Finish
        }
    }
}