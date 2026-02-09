package by.mrrockka.commands.game

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.GameType
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.GameTablesService
import by.mrrockka.service.GameTelegramService
import by.mrrockka.service.PinMessageService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.chain.UserChatReference
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
