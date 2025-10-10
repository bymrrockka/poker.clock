package by.mrrockka.scenario.poll

import by.mrrockka.executor.TelegramTaskExecutor
import by.mrrockka.scenario.AbstractScenarioTest
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractPollScenario : AbstractScenarioTest() {

    @Autowired
    lateinit var taskExecutor: TelegramTaskExecutor

    @BeforeEach
    fun beforePoll() {
        taskExecutor.init()
    }
}