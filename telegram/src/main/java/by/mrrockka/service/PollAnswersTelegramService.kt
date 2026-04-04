package by.mrrockka.service

import by.mrrockka.domain.toPerson
import by.mrrockka.repo.PersonRepo
import by.mrrockka.repo.PollAnswersRepo
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.common.PollAnswer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

interface PollAnswersTelegramService {
    fun store(pollAnswer: PollAnswer, user: User)
}

@Service
@Transactional(propagation = Propagation.REQUIRED)
open class PollAnswersTelegramServiceImpl(
        private val pollAnswersRepo: PollAnswersRepo,
        private val personRepo: PersonRepo,
) : PollAnswersTelegramService {

    override fun store(pollAnswer: PollAnswer, user: User) {
        if (user.username != null) {
            val person = personRepo.findByNickname(user.username!!)
                    .let { person ->
                        person ?: {
                            val new = user.toPerson()
                            personRepo.store(new)
                            new
                        }.invoke()
                    }

            pollAnswersRepo.store(pollAnswer, person)
        } else {
            logger.info { "poll answers skipped because user didn't had username $user" }
        }
    }

}