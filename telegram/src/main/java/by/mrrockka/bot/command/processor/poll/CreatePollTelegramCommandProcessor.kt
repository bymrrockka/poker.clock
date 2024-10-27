package by.mrrockka.bot.command.processor.poll

import by.mrrockka.bot.command.processor.TelegramCommandProcessor
import by.mrrockka.domain.MessageMetadata
import by.mrrockka.service.TaskTelegramService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage

@Component
class CreatePollTelegramCommandProcessor(
        val taskTelegramService: TaskTelegramService
) : TelegramCommandProcessor {

    override fun process(messageMetadata: MessageMetadata): BotApiMethodMessage {
        return taskTelegramService.createPoll(messageMetadata)
    }
}