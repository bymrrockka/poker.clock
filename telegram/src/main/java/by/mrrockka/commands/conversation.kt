package by.mrrockka.commands

import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.chain.Transition
import eu.vendeli.tgbot.types.chain.WizardContext
import eu.vendeli.tgbot.types.chain.WizardStep
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

open class ClearMessageConversation(){

}