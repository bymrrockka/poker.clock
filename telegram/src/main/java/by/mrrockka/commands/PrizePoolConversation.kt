package by.mrrockka.commands

import by.mrrockka.domain.PositionPrize
import by.mrrockka.domain.chat
import by.mrrockka.repo.PinType
import by.mrrockka.service.PinMessageService
import by.mrrockka.service.PrizePoolTelegramService
import eu.vendeli.tgbot.annotations.Guard
import eu.vendeli.tgbot.annotations.WizardHandler
import eu.vendeli.tgbot.api.message.SendMessageAction
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.generated.getState
import eu.vendeli.tgbot.implementations.MapIntStateManager
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.utils.common.send
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@WizardHandler(
        trigger = ["/prize_pool", "/pp"],
        stateManagers = [MapIntStateManager::class, BigDecimalState::class, PositionPrizeState::class],
)
@Guard(ExcludeBotGuard::class)
object PrizePoolConversation : MessageLogConversation() {
    lateinit var prizePoolService: PrizePoolTelegramService
    lateinit var pinMessageService: PinMessageService

    object Size : CancelableStep(isInitial = true, cancelStep = Cancel::class) {
        private fun SendMessageAction.sizeReply(): SendMessageAction = cancelableReplyMarkup {
            +"1"
            +"2"
            +"3"
            +"4"
        }

        override suspend fun onEntry(ctx: WizardContext) =
                with(ctx.session!!) {
                    initialize(ctx)

                    message { "How many places to account?" }
                            .sizeReply()
                            .send(ctx.bot)
                }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) =
                with(ctx.session!!) {
                    message { "Should be a number not less then 1" }
                            .sizeReply()
                            .send(ctx.bot)
                }

        override suspend fun navigate(ctx: WizardContext): Transition = when {
            ctx.update.text.digitValidation() -> Transition.Next
            else -> Transition.Retry()
        }

        override suspend fun store(ctx: WizardContext): Int = ctx.update.text.toInt()
    }

    object PositionPercentage : CancelableStep(isInitial = true, cancelStep = Cancel::class) {
        enum class Navigate {
            PLACE, TOTAL_INVALID, MESSAGE_INVALID;
        }

        private val positionPrizes = ConcurrentHashMap<Long, List<PositionPrize>>()

        private fun Long.get(): List<PositionPrize> = positionPrizes[this] ?: mutableListOf()
        private fun Long.update(text: String): Boolean {
            return if (text.decimalValidation()) {
                val list = this.get()
                val positionPrize = PositionPrize(list.size + 1, text.toBigDecimal())
                positionPrizes[this] = (list + positionPrize)
                true
            } else false
        }

        override suspend fun onEntry(ctx: WizardContext) =
                with(ctx.session!!) {
                    message { "What percentage for #1 place?" }
                            .cancelableReplyMarkup()
                            .send(ctx.bot)
                }

        override suspend fun navigate(ctx: WizardContext): Transition {
            val size = ctx.getState<Size>() ?: error("No size found")
            val updated = ctx.user.id.update(ctx.update.text)
            val percentage = positionPrizes.get(ctx.user.id)?.sumOf { it.percentage }
            val completed = ctx.user.id.get().size == size

            return when {
                completed && percentage != null && percentage != BigDecimal("100") -> Transition.Retry(Navigate.TOTAL_INVALID.name)
                completed -> Transition.Next
                updated -> Transition.Retry(Navigate.PLACE.name)
                else -> Transition.Retry(Navigate.MESSAGE_INVALID.name)
            }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) =
                with(ctx.session!!) {
                    when (reason) {
                        Navigate.PLACE.name -> message { "What percentage for #${ctx.user.id.get().size + 1} place?" }
                                .cancelableReplyMarkup()
                                .send(ctx.bot)

                        Navigate.TOTAL_INVALID.name -> {
                            message { "Position percentage should equal 100% but was ${positionPrizes.remove(ctx.user.id)?.sumOf { it.percentage } ?: 0}%" }
                                    .send(ctx.bot)
                            message { "What percentage for #${ctx.user.id.get().size + 1} place?" }
                                    .cancelableReplyMarkup()
                                    .send(ctx.bot)
                        }

                        else -> message { "Percentage should not be negative" }
                                .cancelableReplyMarkup()
                                .send(ctx.bot)
                    }
                }


        override suspend fun store(ctx: WizardContext): List<PositionPrize> = positionPrizes.remove(ctx.user.id)
                ?: error("No position prizes found for user ${ctx.user.id}")

        override fun beforeCancelAction(ctx: WizardContext) {
            positionPrizes.remove(ctx.user.id)
        }
    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val prizePool = ctx.getState<PositionPercentage>()
                    ?: error("Prize pool not found for user ${ctx.user.id}")
            prizePoolService.store(ctx.initial(), prizePool)
                    .let { prizePool ->
                        message {
                            """
                            |Prize pool stored:
                            |${prizePool.joinToString("\n") { "${it.position}. ${it.percentage}%" }}
                            """.trimMargin()
                        }
                    }.sendReturning(ctx.update.chat(), ctx.bot)
                    .onFailure { error("Failed to send prize pool message") }
                    ?.also { message -> pinMessageService.pin(message, PinType.GAME) }

            ctx.clear()
        }

        override suspend fun validate(ctx: WizardContext): Transition = Transition.Finish
    }

    object Cancel : CancelStep()
}