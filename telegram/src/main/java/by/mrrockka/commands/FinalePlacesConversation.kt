package by.mrrockka.commands

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.chat
import by.mrrockka.domain.toMessageMetadata
import by.mrrockka.repo.PinType
import by.mrrockka.service.FinalePlacesTelegramService
import by.mrrockka.service.PinMessageService
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
import java.util.concurrent.ConcurrentHashMap

@WizardHandler(
        trigger = ["/finale_places", "/fp"],
        stateManagers = [MapIntStateManager::class, PositionMentionState::class],
)
@Guard(ExcludeBotGuard::class)
object FinalePlacesConversation : MessageLogConversation() {
    lateinit var finalePlacesService: FinalePlacesTelegramService
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

    object FinalPlaces : CancelableStep(isInitial = true, cancelStep = Cancel::class) {
        const val nextPercentage = "Next Percentage"
        private val positionToMentions = ConcurrentHashMap<Long, Map<Int, String>>()

        private fun Long.get(): Map<Int, String> = positionToMentions[this] ?: mutableMapOf()
        private fun Long.update(metadata: MessageMetadata): Boolean {
            val mentions = metadata.mentions.toList()
            return if (mentions.size == 1) {
                val map = get()
                val positionToMention = (map.size + 1) to mentions.first().text
                positionToMentions[this] = (map + positionToMention)
                true
            } else false
        }

        override suspend fun onEntry(ctx: WizardContext) =
                with(ctx.session!!) {
                    message { "Who's on #1 place?" }
                            .cancelableReplyMarkup()
                            .send(ctx.bot)
                }

        override suspend fun navigate(ctx: WizardContext): Transition {
            val size = ctx.getState<Size>() ?: error("No size found")

            val updated = ctx.user.id.update(ctx.update.toMessageMetadata())

            return when {
                updated && ctx.user.id.get().size == size -> Transition.Next
                updated -> Transition.Retry(nextPercentage)
                else -> Transition.Retry()
            }
        }

        override suspend fun onRetry(ctx: WizardContext, reason: String?) =
                with(ctx.session!!) {
                    when (reason) {
                        nextPercentage -> message { "Who's on #${ctx.user.id.get().size + 1} place?" }

                        else -> message { "Player mention should be specified" }
                    }
                            .cancelableReplyMarkup()
                            .send(ctx.bot)
                }


        override suspend fun store(ctx: WizardContext): Map<Int, String> = positionToMentions.remove(ctx.user.id)
                ?: error("No final places found for user ${ctx.user.id}")

        override fun beforeCancelAction(ctx: WizardContext) {
            positionToMentions.remove(ctx.user.id)
        }
    }

    object Finish : WizardStep() {
        override suspend fun onEntry(ctx: WizardContext) {
            val positionToMention = ctx.getState<FinalPlaces>()
                    ?: error("Finale Places not found for user ${ctx.user.id}")
            check(positionToMention.values.distinct().size == positionToMention.size) {
                "There are duplicates in finale places"
            }
            finalePlacesService.store(ctx.initial(), positionToMention)
                    .let { finalePlaces ->
                        message {
                            """
                            |Finale places stored:
                            |${finalePlaces.joinToString("\n") { "${it.position}. @${it.person.nickname}" }}
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