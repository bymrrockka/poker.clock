package by.mrrockka.repo

import by.mrrockka.config.TelegramPSQLExtension
import by.mrrockka.creator.ChatCreator
import by.mrrockka.creator.TaskCreator
import by.mrrockka.repo.poll.PollTaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@ExtendWith(TelegramPSQLExtension::class)
@SpringBootTest
@ActiveProfiles("repository")
class PollTaskRepositoryITest {

    @Autowired
    lateinit var repository: PollTaskRepository

    @Test
    fun `given poll task when attempt to store to db should store successfully`() {
        val pollTask = TaskCreator.poll
        repository.upsert(pollTask)
        assertThat(repository.selectNotFinished())
                .containsOnly(pollTask)
    }

    @Test
    fun `given poll tasks when attempt to batch upsert should store successfully`() {
        val tasks = listOf(
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll().copy(chatId = ChatCreator.randomChatId())
        )
        repository.batchUpsert(tasks)
        assertThat(repository.selectNotFinished())
                .containsExactlyInAnyOrderElementsOf(tasks)
    }

    @Test
    fun `given poll tasks when finishes poll should update tasks successfully`() {
        val tasks = listOf(
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll(),
                TaskCreator.randomPoll()
        )
        repository.batchUpsert(tasks)
        repository.finishPoll(tasks[0].messageId, Instant.now())
        val list = repository.selectNotFinished()
        assertAll(
                { assertThat(list).size().isEqualTo(4) },
                { assertThat(list).containsExactlyInAnyOrderElementsOf(tasks - tasks[0]) },
        )
    }
}