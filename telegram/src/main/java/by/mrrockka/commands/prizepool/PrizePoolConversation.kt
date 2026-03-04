package by.mrrockka.commands.prizepool

import by.mrrockka.commands.BigDecimalState
import by.mrrockka.commands.ClearMessageConversation
import by.mrrockka.commands.MessageMetadataState
import by.mrrockka.service.PinMessageService
import by.mrrockka.service.PrizePoolTelegramService
import eu.vendeli.tgbot.annotations.WizardHandler

@WizardHandler(
        trigger = ["/pp"],
        stateManagers = [BigDecimalState::class, MessageMetadataState::class],
)
object PrizePoolConversation : ClearMessageConversation() {
    lateinit var prizePoolService: PrizePoolTelegramService
    lateinit var pinMessageService: PinMessageService
}