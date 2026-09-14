package by.mrrockka.scenario.common

import by.mrrockka.Command
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.WhenSpecification
import by.mrrockka.extension.MdApproverExtension
import by.mrrockka.service.GameTablesService
import com.oneeyedmen.okeydoke.Approver
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
import kotlin.time.ExperimentalTime


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
            val actual = dispatcher.requests.toText()
            val unhandled = commands
                    .mapIndexed { index, command -> index to command }
                    .filter { (index, _) -> !dispatcher.requests.contains(index) }
                    .map { (index, command) -> command.toText(index) }
            val message = """
                |$actual
                |
                |!!!
                |
                |Wanted but not executed:
                |${unhandled.joinToString("\n-------")}
                |
                |!!!
                |
                |Await timeout
                |Dispatcher requests size is ${dispatcher.requests.size}
                |Commands size is ${filtered.size}
                |Dispatcher should have exactly the same requests size as commands size.
                """.trimMargin()
            approver.writeActual(actual)
            throw ConditionTimeoutException("${ex.message}\n\n$message")
        }

        dispatcher.requests.toText()
                .also { approver.assertApproved(it) }
    }

    private fun Approver.writeActual(actual: String) {
        writeFormatted(actual)
        outputStream().close()
    }

    private fun Map<Int, String>.toText(): String = values.joinToString("\n\n")

    private fun Command.toText(index: Int): String {
        return when (this) {
            is Command.UserMessage ->
                """
                |# Interaction $index
                |User @${username ?: mainUser.username} -> 
                |```
                |$message
                |```
                """.trimMargin()

            is Command.BotMessage ->
                """
                |# Interaction $index
                |Bot -> 
                |```
                |$message
                |```
                """.trimMargin()

            is Command.Poll ->
                """
                |# Interaction $index
                |Poll -> $time"
                """.trimMargin()

            is Command.PollAnswer -> """
                |# Interaction $index
                |User @${this.person.nickname} poll answer -> $option"
                """.trimIndent()

            is Command.Pin ->
                """
                |# Interaction $index
                |Pin -> 
                |```
                |${messageLog[command]?.messageId ?: "No message found"}
                |```
                """.trimMargin()

            is Command.Unpin ->
                """
                |# Interaction $index
                |Unpin -> 
                |```
                |${messageLog[command]?.messageId ?: "No message found"}
                |```
                """.trimMargin()

            is Command.DeleteMessages ->
                """
                |# Interaction $index
                |Delete messages -> ${toDelete.map { messageLog[it]?.messageId }.joinToString(", ")}
                """.trimMargin()

            else -> "Message $index type is not found $this"
        }
    }

}