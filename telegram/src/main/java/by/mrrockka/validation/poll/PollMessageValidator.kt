package by.mrrockka.validation.poll

import by.mrrockka.domain.PollTask
import org.springframework.stereotype.Component

@Component
class PollMessageValidator {
    fun validatePoll(toPollTask: PollTask) {
        throw UnsupportedOperationException("Not supported yet.")
    }
}
