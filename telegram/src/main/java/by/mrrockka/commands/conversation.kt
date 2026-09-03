package by.mrrockka.commands

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.toMessageMetadata
import eu.vendeli.tgbot.api.message.SendMessageAction
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.interfaces.session.Session
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
import eu.vendeli.tgbot.utils.builders.ReplyKeyboardMarkupBuilder
import eu.vendeli.tgbot.utils.common.send
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
                selective = true
            }
        }
    }
}

open class CancelStep : WizardStep() {
    override suspend fun onEntry(ctx: WizardContext) {
        with(ctx.session) {
            checkNotNull(this) { error("Session not found") }
            message { "Game creation was cancelled" }.send(ctx.bot)
            clear(bot)
        }
    }

    override suspend fun validate(ctx: WizardContext): Transition {
        return Transition.Finish
    }
}

fun String.decimalValidation() = matches("^([\\d.]+)$".toRegex())
fun String.digitValidation() = matches("^([\\d]+)$".toRegex())

abstract class MessageLogConversation {
    private val initials = ConcurrentHashMap<Long, MessageMetadata>()

    protected fun Session.initialize(ctx: WizardContext) {
        initials += (userId ?: chatId) to ctx.update.toMessageMetadata()
    }

    protected fun WizardContext.initial(): MessageMetadata = with(session!!) {
        initials[userId ?: chatId] ?: error("Initial message not found for user $this")
    }

    protected suspend fun WizardContext.clear() {
        with(session) {
            checkNotNull(this) { error("Session not found") }
            clear(bot)
            initials.remove(userId ?: chatId)
        }
    }
}