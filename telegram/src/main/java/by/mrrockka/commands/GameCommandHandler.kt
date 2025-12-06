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
import eu.vendeli.tgbot.annotations.InputChain
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.deleteMessage
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.generated.getAllState
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.chain.BaseStatefulLink
import eu.vendeli.tgbot.types.chain.BreakCondition
import eu.vendeli.tgbot.types.chain.ChainAction
import eu.vendeli.tgbot.types.chain.ChainLink
import eu.vendeli.tgbot.types.chain.ChainingStrategy
import eu.vendeli.tgbot.types.component.MessageUpdate
import eu.vendeli.tgbot.types.component.ProcessedUpdate
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.utils.common.setChain
import org.springframework.stereotype.Component

interface GameCommandHandler {
    suspend fun store(message: MessageUpdate)
    suspend fun game(message: MessageUpdate, user: User)
    suspend fun gameConversation(message: MessageUpdate)
    suspend fun game1(message: MessageUpdate, user: User)
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

    @CommandHandler(["/gam"])
    override suspend fun game(message: MessageUpdate, user: User) {
        sendMessage { "What type of game you'd like to play?" }
                .sendReturning(to = message.chat.id, via = bot)
                .onFailure { error("Failed to send message") }
                ?.also { message ->
                    bot.inputListener[user] = "gam"
                    deleteMessage(message.messageId).send(to = message.chat.id, via = bot)
                }
    }

    @InputHandler(["gam"])
    override suspend fun gameConversation(message: MessageUpdate) {
        sendMessage { " Text : ${message.text}" }
                .send(to = message.chat.id, via = bot)
    }

    @CommandHandler(["/game"])
    override suspend fun game1(message: MessageUpdate, user: User) {
        sendMessage { "What type of game you'd like to play?" }
                .send(to = message.chat.id, via = bot)
                .also { message ->
                    bot.inputListener.setChain(user, GameChain.Controller)
                }
    }
}

@InputChain
object GameChain {
    /*
    * Worth to create some controller which routes to specific steps in order to define bounty which is not necessary for most of the games
    * */

    object Type : BaseStatefulLink() {
        override val breakCondition = BreakCondition { _, update, _ -> GameType.entries.find { it.name.equals(update.text, ignoreCase = true) } == null }
        override suspend fun breakAction(user: User, update: ProcessedUpdate, bot: TelegramBot) {
            message { "Type should be one of ${GameType.entries.joinToString { it.name.lowercase() }}" }.send(user, bot)
        }

        override suspend fun action(user: User, update: ProcessedUpdate, bot: TelegramBot): String {
            message { "How much is for buy in?" }.send(user, bot)

            return update.text
        }
    }

    object Buyin : BaseStatefulLink() {
        private var gameType: GameType? = null
        override val breakCondition = BreakCondition { _, update, _ -> !update.text.matches("^([\\d]+)$".toRegex()) }
        override suspend fun breakAction(user: User, update: ProcessedUpdate, bot: TelegramBot) {
            message { "Buy in is necessary for calculations and it should be a number" }.send(user, bot)
        }

        override suspend fun action(user: User, update: ProcessedUpdate, bot: TelegramBot): String {
            gameType = GameType.valueOf(
                    user.getAllState(GameChain).Type?.uppercase()
                            ?: error("No default game type set up"),
            )
            return update.text
        }

        override val afterAction = ChainAction { user, update, bot ->
            val type = GameType.valueOf(
                    user.getAllState(GameChain).Type?.uppercase()
                            ?: error("No default game type set up"),
            )
            if (type == GameType.BOUNTY) {
                message { "As it's bounty game we also need bounty buy in" }.send(user, bot)
            }
        }

        override val chainingStrategy: ChainingStrategy
            get() {
                return if (gameType == GameType.BOUNTY) ChainingStrategy.LinkTo { Bounty }
                else ChainingStrategy.DoNothing
            }
    }

    object Bounty : BaseStatefulLink() {
        override val breakCondition = BreakCondition { _, update, _ -> !update.text.matches("^([\\d]+)$".toRegex()) }
        override suspend fun breakAction(user: User, update: ProcessedUpdate, bot: TelegramBot) {
            message { "Bounty is necessary for Bounty tournament and it should be a number" }.send(user, bot)
        }

        override suspend fun action(user: User, update: ProcessedUpdate, bot: TelegramBot): String {
            return update.text
        }

    }

    object Controller : ChainLink() {
        override suspend fun action(user: User, update: ProcessedUpdate, bot: TelegramBot) {

//            val type = user.getAllState(GameChain).Type

        }
    }
}