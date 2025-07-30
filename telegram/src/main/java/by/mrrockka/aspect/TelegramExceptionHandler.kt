package by.mrrockka.aspect

import by.mrrockka.bot.PokerClockAbsSender
import lombok.RequiredArgsConstructor
import lombok.SneakyThrows
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface TelegramExceptionHandler {
    fun handleExceptions(exception: Throwable, updates: List<Update>)
}

@Aspect
@Component
@RequiredArgsConstructor
@Profile("!no-exception-handler")
class TelegramExceptionHandlerImpl(
        private val absSender: PokerClockAbsSender,
) : TelegramExceptionHandler {

    @SneakyThrows
    @AfterThrowing(
            pointcut = "execution(* *.onUpdatesReceived(..)) && args(updates)",
            argNames = "exception,updates",
            throwing = "exception",
    )
    override fun handleExceptions(exception: Throwable, updates: List<Update>) {
        val sendMessage = SendMessage().apply {
            this.chatId = getChatId(updates).toString()
            this.text = exception.message ?: exception.javaClass.simpleName
        }

        absSender.execute(sendMessage)
    }

    private fun getChatId(updates: List<Update>): Long =
            updates.find { it?.message?.chatId != null }?.message?.chatId ?: error("No chat id found for update")

}
