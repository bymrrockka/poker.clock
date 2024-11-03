package by.mrrockka.validation.poll

import by.mrrockka.domain.PollTask
import by.mrrockka.exception.BusinessException
import org.springframework.stereotype.Component

@Component
class PollMessageValidator {
    fun validatePoll(pollTask: PollTask) {
        when {
            pollTask.message.isBlank() -> throw PollHasNoRequiredFieldException("message")
            pollTask.options.isEmpty() -> throw PollHasNoRequiredFieldException("options")
        }
    }
}

internal class PollHasNoRequiredFieldException(private val field: String) : BusinessException("Poll $field is required.")
