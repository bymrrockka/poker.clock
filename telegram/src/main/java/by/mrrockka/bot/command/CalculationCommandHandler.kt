package by.mrrockka.bot.command

import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.response.builder.calculation
import by.mrrockka.service.CalculationTelegramService
import by.mrrockka.service.GameTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.component.MessageUpdate
import org.springframework.stereotype.Component

interface CalculationCommandHandler {
    suspend fun calculate(message: MessageUpdate)
}

@Component
class CalculationCommandHandlerImpl(
        private val bot: TelegramBot,
        private val calculationService: CalculationTelegramService,
        private val gameService: GameTelegramService,
) : CalculationCommandHandler {

    @CommandHandler(["/calculate"])
    override suspend fun calculate(message: MessageUpdate) {
        val metadata = message.message.toMessageMetadata()
        val telegramGame = gameService.findGame(metadata)
        calculationService.calculatePayouts(metadata)
                .also { payouts ->
                    sendMessage {
                        calculation {
                            game(telegramGame.game)
                            payouts(payouts)
                        }.response()
                    }.send(metadata.chatId, via = bot)
                }
//  todo:  pin message
    }


}