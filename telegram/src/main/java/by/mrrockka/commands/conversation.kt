package by.mrrockka.commands

import by.mrrockka.commands.game.GameConversation
import by.mrrockka.domain.MessageMetadata
import eu.vendeli.tgbot.api.message.deleteMessages
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.types.msg.Message
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

abstract class CancelableStep(isInitial: Boolean = false, val cancelStep: KClass<out WizardStep>) : WizardStep(isInitial) {
    private fun String.canceled() = matches("^cancel$".toRegex())

    abstract suspend fun navigate(ctx: WizardContext): Transition
    override suspend fun validate(ctx: WizardContext): Transition {
        return when {
            ctx.update.text.canceled() -> Transition.JumpTo(cancelStep, skipPersist = true)
            else -> navigate(ctx)
        }
    }

    suspend fun WizardContext.initialize(postAction: suspend (Message) -> Unit) {
        message {
            """
            |You started a cancelable conversation. 
            |To cancel at any step you simply need to type 'cancel'
        """.trimMargin()
        }.sendReturning(user, bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> postAction(message) }
    }

}

open class CancelStep(
        val postAction: suspend (WizardContext) -> Unit,
) : WizardStep() {
    override suspend fun onEntry(ctx: WizardContext) {
        message { "Game creation was cancelled" }.send(ctx.user, ctx.bot)
        postAction(ctx)
    }

    override suspend fun validate(ctx: WizardContext): Transition {
        return Transition.Finish
    }
}

fun String.decimalValidation() = matches("^([\\d.]+)$".toRegex())

abstract class ClearMessageConversation {
    protected val messages = ConcurrentHashMap<Long, List<Long>>()
    protected val initials = ConcurrentHashMap<Long, MessageMetadata>()

    protected fun Long.message(messageId: Long) {
        synchronized(messages) {
            val list = GameConversation.messages[this]
            if (list != null) {
                GameConversation.messages[this] = (list + messageId)
            } else {
                GameConversation.messages[this] = mutableListOf(messageId)
            }
        }
    }

    protected suspend fun WizardContext.clearMessages() {
        messages[user.id]?.also { list ->
            deleteMessages(list)
                    .sendReturning(user, bot)
                    .onFailure { "Failed to clear messages" }
                    ?.also { messages.remove(user.id) }
        }
    }
}