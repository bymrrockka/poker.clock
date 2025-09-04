package by.mrrockka.bot.command

import by.mrrockka.domain.BountyTournamentGame
import by.mrrockka.domain.CashGame
import by.mrrockka.domain.TournamentGame
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.service.GameTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface GameCommandHandler {
    suspend fun store(message: MessageUpdate)
}

@Component
class GameCommandHandlerImpl(
        private val bot: TelegramBot,
        private val gameService: GameTelegramService,
) : GameCommandHandler {

    @CommandHandler(["/tournament_game", "/bounty_game", "/cash_game"])
    override suspend fun store(message: MessageUpdate) {
        gameService.storeGame(message.message.toMessageMetadata())
                .let { game ->
                    when (game) {
                        is CashGame -> "Cash game started."
                        is TournamentGame -> "Tournament game started."
                        is BountyTournamentGame -> "Bounty tournament game started."
                        else -> error("Game type not supported: ${this.javaClass.simpleName}")
                    }
                }.also { response ->
                    sendMessage { response }.send(to = message.message.chat.id, bot)
                }

        //todo: add pin to incomming message
    }
}