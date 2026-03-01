package by.mrrockka.scenario

import by.mrrockka.Command
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.GivenSpecification
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.WhenSpecification
import by.mrrockka.builder.message
import by.mrrockka.builder.toUser
import by.mrrockka.builder.update
import by.mrrockka.builder.user
import by.mrrockka.extension.MdApproverExtension
import by.mrrockka.repo.BountyTable
import by.mrrockka.repo.ChatGameTable
import by.mrrockka.repo.ChatPersonsTable
import by.mrrockka.repo.ChatPollsTable
import by.mrrockka.repo.EntriesTable
import by.mrrockka.repo.FinalePlacesTable
import by.mrrockka.repo.GameSummaryTable
import by.mrrockka.repo.GameTable
import by.mrrockka.repo.GameTablesTable
import by.mrrockka.repo.PersonTable
import by.mrrockka.repo.PinMessageTable
import by.mrrockka.repo.PollAnswersTable
import by.mrrockka.repo.PollTaskTable
import by.mrrockka.repo.PrizePoolTable
import by.mrrockka.repo.WithdrawalTable
import by.mrrockka.service.GameTablesService
import com.oneeyedmen.okeydoke.Approver
import eu.vendeli.tgbot.types.msg.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.DependsOn
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.junit.jupiter.Testcontainers
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
@Testcontainers
@SpringBootTest(classes = [TestConfig::class])
abstract class AbstractScenarioTest {
    private val randoms = telegramRandoms("scenario")
    private val chatid = randoms.chatid()
    private val user = user(randoms)
    private val messageLog = mutableMapOf<Command, Message>()

    @Autowired
    lateinit var dispatcher: MockDispatcher

    @Autowired
    lateinit var clock: TestClock

    @Autowired
    lateinit var gameSeatsService: GameTablesService

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    @BeforeEach
    fun before() {
        gameSeatsService.seed(telegramRandoms.seed.hashCode().toLong())
    }

    @AfterEach
    fun after() {
        coreRandoms.reset()
        telegramRandoms.reset()
        dispatcher.reset()
        transaction {
            PinMessageTable.deleteAll()
            ChatPersonsTable.deleteAll()
            ChatGameTable.deleteAll()
            PollAnswersTable.deleteAll()
            ChatPollsTable.deleteAll()
            PollTaskTable.deleteAll()

            GameTablesTable.deleteAll()
            GameSummaryTable.deleteAll()
            BountyTable.deleteAll()
            EntriesTable.deleteAll()
            WithdrawalTable.deleteAll()
            PrizePoolTable.deleteAll()
            FinalePlacesTable.deleteAll()
            PersonTable.deleteAll()
            GameTable.deleteAll()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun GivenSpecification.updatesReceived() {
        check(commands.isNotEmpty()) { "Commands should be specified" }
        commands.forEachIndexed { index, command -> command.stub(index) }
    }

    infix fun WhenSpecification.ThenApproveWith(approver: Approver) {
        val filteredCommands = commands.filter { it !is Command.PollAnswer }
        try {
            await atMost Duration.ofSeconds(5) until {
                dispatcher.requests.size == filteredCommands.size
            }
        } catch (ex: Exception) {
            logger.error { "Await timeout failed" }
        }

        commands.toText()
                .also { approver.assertApproved(it.trim()) }
    }

    private fun List<Command>.toText(): String {
        val emptyMessage = "No message"
        return mapIndexed { index, command ->
            when (command) {
                is Command.Message ->
                    """
                   |### ${index + 1}. Interaction
                   |
                   |&rarr; <ins>User message</ins>
                   |
                   |```
                   |${command.toText()} 
                   |```
                   |
                   |&rarr; <ins>Bot message</ins>
                   |
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

                is Command.DeleteMessages ->
                    """
                   |### ${index + 1}. Deleted messages
                   |
                   |``` 
                   |${command.toText()} ${dispatcher.requests[index] ?: emptyMessage}
                   |``` 
                   |___
                   """.trimMargin()

                else -> error("<p style=\"color:red\">Command type is not found</p>")
            }
        }.joinToString("\n\n")
    }

    private fun Command.toText(): String {
        return when (this) {
            is Command.Message -> {
                val replyMessage = if (replyTo != null && messageLog[replyTo] != null) "[reply to message id ${messageLog[replyTo]!!.messageId}]\n" else ""
                replyMessage + """
                            |message id: ${messageLog[this]!!.messageId}
                            |$message
                        """.trimMargin()
            }

            is Command.PollAnswer -> "${person.nickname} chosen ${option}"
            is Command.Pin -> "message id ${messageLog[command]?.messageId ?: error("Command was not found in log")}"
            is Command.Unpin -> "message id ${messageLog[command]?.messageId ?: error("Command was not found in log")}"
            is Command.Poll -> "message id ${messageLog[this]?.messageId ?: error("Command was not found in log")}"
            is Command.DeleteMessages -> messageLog
                    .filter { (key, _) -> toDelete.contains(key) }
                    .values
                    .map { it.messageId }
                    .joinToString(",")
                    .also {
                        if (it.isEmpty()) error("Command was not found in log")
                        "message ids ${it}"
                    }

            else -> error("Command type does not found")
        }
    }

    private fun Command.stub(index: Int) {
        when (this) {
            is Command.PollAnswer -> {
                val update = update {
                    pollAnswer {
                        pollId(messageLog[this@stub.poll]!!.poll!!.id)
                        option(option - 1)
                        user(person.toUser())
                    }
                }

                dispatcher.scenario {
                    index(index)
                    update(update)
                }
            }

            is Command.Message -> {
                val update = update {
                    message {
                        text(message)
                        chatId(chatid)
                        from(user)
                        createdAt(clock.now().toJavaInstant())
                        if (replyTo != null && messageLog[replyTo] != null) {
                            replyTo(messageLog[replyTo]!!)
                        }
                    }
                }

                messageLog += this to update.message!!

                dispatcher.scenario {
                    index(index)
                    update(update)
                    message(update.message!!)
                }
            }

            is Command.Poll -> {
                val message = message {
                    chatId(chatid)
                    poll()
                }
                messageLog += this to message

                dispatcher.scenario {
                    index(index)
                    poll(message)
                    time(time)
                }
            }

            is Command.Pin -> {
                dispatcher.scenario {
                    index(index)
                    pin()
                }
            }

            is Command.Unpin -> {
                dispatcher.scenario {
                    index(index)
                    unpin()
                }
            }

            is Command.DeleteMessages -> {
                dispatcher.scenario {
                    index(index)
                    delete()
                }
            }

            else -> error("Command type haven't recognised")
        }
    }
}