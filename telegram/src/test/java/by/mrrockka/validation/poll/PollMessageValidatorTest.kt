package by.mrrockka.validation.poll

import by.mrrockka.creator.TaskCreator
import by.mrrockka.domain.PollTask
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class PollMessageValidatorTest {

    private val validator = PollMessageValidator()

    @ParameterizedTest
    @MethodSource("pollTasks")
    fun `should raise an exception when any of the fields is missing`(pollTask: PollTask, field: String) {
        assertThrows<PollHasNoRequiredFieldException>("Poll $field is required.") {
            validator.validatePoll(pollTask)
        }
    }

    @Test
    fun `should not fail with exception when all fields are present`() {
        val pollTask = TaskCreator.poll
        assertDoesNotThrow {
            validator.validatePoll(pollTask)
        }
    }

    companion object {
        @JvmStatic
        fun pollTasks() = listOf(
                Arguments.of(TaskCreator.poll.copy(message = ""), "message"),
                Arguments.of(TaskCreator.poll.copy(options = listOf()), "options")
        )
    }
}