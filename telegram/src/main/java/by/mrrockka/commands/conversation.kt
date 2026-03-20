package by.mrrockka.commands

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.chat
import by.mrrockka.domain.toMessageMetadata
import eu.vendeli.tgbot.api.message.SendMessageAction
import eu.vendeli.tgbot.api.message.deleteMessages
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.types.component.onFailure
import eu.vendeli.tgbot.utils.builders.ReplyKeyboardMarkupBuilder
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

abstract class CancelableStep(isInitial: Boolean = false, val cancelStep: KClass<out WizardStep>) : WizardStep(isInitial) {
    private val cancel = "cancel"
    private fun String.canceled() = matches("^${cancel}$".toRegex())

    open fun beforeCancelAction(ctx: WizardContext) {}
    abstract suspend fun navigate(ctx: WizardContext): Transition
    override suspend fun validate(ctx: WizardContext): Transition {
        return when {
            ctx.update.text.canceled() -> {
                beforeCancelAction(ctx)
                Transition.JumpTo(cancelStep, skipPersist = true)
            }

            else -> navigate(ctx)
        }
    }

    protected fun SendMessageAction.cancelableReplyMarkup(block: ReplyKeyboardMarkupBuilder.() -> Unit = {}): SendMessageAction {
        return replyKeyboardMarkup {
            block()
            +cancel
            options {
                resizeKeyboard = true
                oneTimeKeyboard = true
            }
        }
    }
}

open class CancelStep(
        val postAction: suspend (WizardContext) -> Unit,
) : WizardStep() {
    override suspend fun onEntry(ctx: WizardContext) {
        message { "Game creation was cancelled" }.send(to = ctx.update.chat(), ctx.bot)
        postAction(ctx)
    }

    override suspend fun validate(ctx: WizardContext): Transition {
        return Transition.Finish
    }
}

fun String.decimalValidation() = matches("^([\\d.]+)$".toRegex())
fun String.digitValidation() = matches("^([\\d]+)$".toRegex())

abstract class MessageLogConversation {
    private val messages = ConcurrentHashMap<Long, List<Long>>()
    private val initials = ConcurrentHashMap<Long, MessageMetadata>()

    protected suspend fun SendMessageAction.sendLogging(ctx: WizardContext) {
        sendReturning(ctx.update.chat(), ctx.bot)
                .onFailure { error("Failed to send message") }
                ?.also { message -> ctx.user.id.message(message.messageId) }
    }

    protected fun Long.message(messageId: Long) {
        synchronized(messages) {
            val list = messages[this]
            if (list != null) {
                messages[this] = (list + messageId)
            } else {
                messages[this] = mutableListOf(messageId)
            }
        }
    }

    protected fun WizardContext.initialize(): MessageMetadata {
        val metadata = update.toMessageMetadata()
        initials += user.id to metadata
        return metadata
    }

    protected fun Long.initial(): MessageMetadata = initials[this] ?: error("Initial message not found for user $this")

    protected suspend fun WizardContext.clearMessages() {
        initials.remove(user.id)
        messages.remove(user.id)?.also { list ->
            deleteMessages(list)
                    .sendReturning(update.chat(), bot)
                    .onFailure { "Failed to clear messages" }
                    ?.also { messages.remove(user.id) }
        }
    }
}