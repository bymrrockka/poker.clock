package by.mrrockka.scenario.common

import by.mrrockka.Command
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.WhenSpecification
import by.mrrockka.extension.MdApproverExtension
import by.mrrockka.service.GameTablesService
import com.oneeyedmen.okeydoke.Approver
import io.github.oshai.kotlinlogging.KotlinLogging
import org.awaitility.core.ConditionTimeoutException
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalTime::class)
@ExtendWith(value = [MdApproverExtension::class])
@ActiveProfiles(profiles = ["scenario"])
@DependsOn("mockWebServer")
abstract class ScenarioTest : StubTest() {
    @Autowired
    lateinit var gameSeatsService: GameTablesService

    @BeforeEach
    fun before() {
        dispatcher.reset()
        gameSeatsService.seed(telegramRandoms.seed.hashCode().toLong())
    }

    @AfterEach
    fun after() {
        transaction {
            exec("TRUNCATE TABLE pin_messages, poll_task, person, game, chat_messages CASCADE")
        }
    }

    infix fun WhenSpecification.ThenApproveWith(approver: Approver) {
        val filtered = commands.filter { it !is Command.Member }
        try {
            await atMost Duration.ofSeconds(5) until {
                dispatcher.requests.size == filtered.size
            }
        } catch (ex: ConditionTimeoutException) {
            val message = """
                |Await timeout
                |Dispatcher requests size is ${dispatcher.requests.size}
                |Commands size is ${filtered.size}
                |Dispatcher should have exactly the same requests size as commands size.
                """.trimMargin()
            throw ConditionTimeoutException("${ex.message}\n\n$message", ex)
        }

        commands.toText()
                .also { approver.assertApproved(it.trim()) }
    }

    private fun List<Command>.toText(): String {
        val emptyMessage = "No message"
        return mapIndexed { index, command ->
            when (command) {
                is Command.UserMessage ->
                    """
                   |### ${index + 1}. Message
                   |
                   |&rarr; <ins>User</ins>
                   |
                   |```
                   |${command.toText()} 
                   |```
                   |___
                   """.trimMargin()

                is Command.BotMessage ->
                    """
                   |### ${index + 1}. Message
                   |
                   |&larr; <ins>Bot</ins>
                   |``` 
                   |${dispatcher.requests[index] ?: emptyMessage} 
                   |``` 
                   |___
                   """.trimMargin()

                is Command.Poll -> {
                    val dateTime = LocalDateTime.ofInstant(command.time.toJavaInstant(), ZoneId.systemDefault())
                    """
                   |### ${index + 1}. Posted
                   |
                   |&rarr; <ins>${dateTime.toLocalDate()} - ${dateTime.dayOfWeek}</ins>
                   |
                   |``` 
                   |${command.toText()}
                   |${dispatcher.requests[index] ?: emptyMessage}
                   |``` 
                   |___
                   """.trimMargin()
                }

                is Command.PollAnswer ->
                    """
                   |### ${index + 1}. Poll answer
                   |
                   |``` 
                   |${command.toText()}
                   |``` 
                   |___
                   """.trimMargin()

                is Command.Pin ->
                    """
                   |### ${index + 1}. Pinned
                   |
                   |``` 
                   |${command.toText()} ${dispatcher.requests[index] ?: emptyMessage}
                   |``` 
                   |___
                   """.trimMargin()

                is Command.Unpin ->
                    """
                   |### ${index + 1}. Unpinned
                   |
                   |``` 
                   |${command.toText()} ${dispatcher.requests[index] ?: emptyMessage}
                   |``` 
                   |___
                   """.trimMargin()

                is Command.DeleteMessages -> {
                    val request = dispatcher.requests[index]
                    """
                   |### ${index + 1}. Deleted messages
                   |
                   |``` 
                   |${
                        if (request != null) {
                            "${command.toText()} $request"
                        } else {
                            "Were not deleted"
                        }
                    }
                   |``` 
                   |___
                   """.trimMargin()
                }

                is Command.Member -> """
                   |### ${index + 1}. Member requested
                   |___
                """.trimMargin()

                else -> error("<p style=\"color:red\">Command type is not found</p>")
            }
        }.joinToString("\n\n")
    }

    private fun Command.toText(): String {
        return when (this) {
            is Command.UserMessage -> {
                val replyMessage = if (replyTo != null && messageLog[replyTo] != null) "[reply to message id ${messageLog[replyTo]!!.messageId}]\n" else ""
                replyMessage + """
                            |$message
                        """.trimMargin()
            }

            is Command.BotMessage -> {
                if (replyTo != null && messageLog[replyTo] != null) "[reply to message id ${messageLog[replyTo]!!.messageId}]\n" else ""
            }

            is Command.PollAnswer -> "${person.nickname} chosen ${option}"
            is Command.Pin -> "message id ${messageLog[command]?.messageId ?: error("Command was not found in log")}"
            is Command.Unpin -> "message id ${messageLog[command]?.messageId ?: error("Command was not found in log")}"
            is Command.Poll -> ""
            is Command.DeleteMessages -> messageLog
                    .filter { (key, _) -> toDelete.contains(key) }
                    .values
                    .map { it.messageId }
                    .sorted()
                    .joinToString(",")
                    .let {
                        if (it.isEmpty()) error("Command was not found in log")
                        "message ids ${it}"
                    }

            else -> error("Command type does not found")
        }
    }
}