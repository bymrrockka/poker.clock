package by.mrrockka.executor

import by.mrrockka.bot.PokerClockAbsSender
import by.mrrockka.domain.Task
import by.mrrockka.service.TaskTelegramService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TaskExecutor(
        private val taskTelegramService: TaskTelegramService,
        private val pokerClockAbsSender: PokerClockAbsSender
) {

    private fun List<Task>.applicable() = asSequence()
            .filter { it -> true } //todo: add filter by deleted

    @Scheduled(fixedRate = 1000L)
    fun execute() {
        taskTelegramService.getTasks()
                .applicable()
                .forEach { task ->
                    pokerClockAbsSender.executeAsync(task.toMessage())//todo: here is a place where we can get message id for poll
                }
    }

}