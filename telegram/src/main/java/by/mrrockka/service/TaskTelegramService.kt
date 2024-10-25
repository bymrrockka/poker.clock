package by.mrrockka.service

import by.mrrockka.domain.MessageMetadata
import by.mrrockka.domain.PollTask
import by.mrrockka.domain.Task
import by.mrrockka.repo.poll.PollTaskRepository
import by.mrrockka.repo.task.ForcedBetsRepository
import by.mrrockka.validation.poll.PollMessageValidator
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.*

@Service
class TaskTelegramService(
        private val pollMessageValidator: PollMessageValidator,
        private val pollTaskRepository: PollTaskRepository,
        private val forcedBetsRepository: ForcedBetsRepository
) {
    var tasks: Map<UUID, Task> = mapOf();

    @PostConstruct
    fun init() {
        assignTasks()
    }

    fun createPoll(messageMetadata: MessageMetadata): BotApiMethodMessage {
        pollMessageValidator.validatePoll()
        pollTaskRepository.upsert(PollTask())

        /* todo:
        *   - validate message
        *   - store to database???
        *   - return message
        * */

        return SendMessage().apply {
            chatId = messageMetadata.chatId.toString()

        }
    }

    fun getTasks(): List<Task> {
        return tasks.values.toList().orEmpty();
    }

    private fun assignTasks() {
        tasks = (forcedBetsRepository.getAllNotDeleted() + pollTaskRepository.selectNotFinished())
                .associateBy({ it.id }, { it })
    }


}
