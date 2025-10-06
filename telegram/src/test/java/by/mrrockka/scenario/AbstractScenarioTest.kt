package by.mrrockka.scenario

import by.mrrockka.BotProperties
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
import by.mrrockka.extension.TelegramPSQLExtension
import by.mrrockka.extension.TelegramWiremockContainer
import by.mrrockka.extension.TelegramWiremockExtension
import by.mrrockka.scenario.Commands.Companion.chatPoll
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import com.marcinziolo.kotlin.wiremock.and
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.oneeyedmen.okeydoke.Approver
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.msg.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.DependsOn
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalTime::class)
@ExtendWith(value = [TelegramPSQLExtension::class, TelegramWiremockExtension::class, MdApproverExtension::class])
@ActiveProfiles(profiles = ["scenario"])
@DependsOn("mockWebServer")
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {
    private val randoms = telegramRandoms("scenario")
    private val chatid = randoms.chatid()
    private val user = user(randoms)
    private val messageLog = mutableMapOf<String, Message>()

    @Autowired
    lateinit var dispatcher: MockDispatcher

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: BotProperties

    @Autowired
    lateinit var clock: TestClock

    private fun String.toJson(): JsonNode = mapper.readTree(this)

    @OptIn(DelicateCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun after() {
        coreRandoms.reset()
        telegramRandoms.reset()
        dispatcher.reset()
    }

    companion object {
        lateinit var wireMock: WireMock
        val mockMessageResponse = Response.Success("TEST OK")

        @OptIn(KtGramInternal::class)
        val getUpdates = GetUpdatesAction().run { methodName }

        @OptIn(KtGramInternal::class)
        val sendMessage = sendMessage("").run { methodName }

        @OptIn(KtGramInternal::class)
        val sendPoll = poll("", emptyList()).run { methodName }

        @OptIn(KtGramInternal::class)
        val pinChatMessage = pinChatMessage(-1L).run { methodName }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            wireMock = WireMock(TelegramWiremockContainer.port)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
        }

        @OptIn(ExperimentalSerializationApi::class)
        internal val serde = Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun GivenSpecification.updatesReceived(chatId: Long = chatid) {
        check(this.commands.isNotEmpty()) { "Commands should be specified" }

        //starts a scenario by moving state
        wireMock.post {
            url equalTo "${botProps.botpath}/${getUpdates}"
        } returnsJson {
            body = serde.encodeToString(Response.Success(emptyList<Update>()))
        } and {
            toState = "${scenarioSeed}0"
        }

        //ends a scenario when all commands were completed
        wireMock.post {
            url equalTo "${botProps.botpath}/${getUpdates}"
            whenState = "${scenarioSeed}${commands.size}"
        } returnsJson {
            body = serde.encodeToString(Response.Success(emptyList<Update>()))
        }

        //todo: find a way to log pinned messages
        commands.forEachIndexed { index, command ->
            when (command) {
                is Command.Message -> command.stub(index, chatId)
                is Command.Poll -> command.stub(index)
                is Command.PollAnswer -> command.stub(index)
                is Command.PinMessage -> command.stub(index)
            }
        }

    }

    infix fun WhenSpecification.ThenApproveWith(approver: Approver) {
        val filteredCommands = commands.filter { it !is Command.PollAnswer }
        try {
            await atMost Duration.ofSeconds(3) until {
                dispatcher.requests.size == filteredCommands.size
            }
        } catch (ex: Exception) {
            logger.error { "Await timeout failed" }
        }

        commands.toText()
                .also { approver.assertApproved(it.trim()) }
    }

//    private fun requests(): Map<Int, String> = tg.takeRequest()

    private fun List<Command>.toText(): String {
        val errorMessage = "No message"
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
                   |${dispatcher.requests[index] ?: errorMessage} 
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
                   |${dispatcher.requests[index] ?: errorMessage}
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

                is Command.PinMessage ->
                    """
                   |### ${index + 1}. Posted
                   |
                   |``` 
                   |${command.toText()} ${dispatcher.requests[index] ?: errorMessage}
                   |``` 
                   |___
                   """.trimMargin()

                else -> error("<p style=\"color:red\">Command type is not found</p>")
            }
        }.joinToString("\n\n")
    }


    private fun Command.Message.toText(): String = "${if (!replyTo.isNullOrBlank()) "[reply to ${replyTo}]\n" else ""}$message"
    private fun Command.PollAnswer.toText(): String = "${this.person.nickname} chosen ${this.option}"
    private fun Command.PinMessage.toText(): String = message

    private fun Command.Message.stub(index: Int, chatId: Long) {
        val update = update {
            message {
                text(message)
                chatId(chatId)
                from(user)
                createdAt(clock.now().toJavaInstant())
                if (replyTo != null && messageLog[replyTo] != null) {
                    replyTo(messageLog[replyTo]!!)
                }
            }
        }

        messageLog += botcommand to update.message!!

        dispatcher.scenario {
            index(index)
            update(update)
            message()
        }
    }

    private fun Command.PollAnswer.stub(index: Int) {
        val update = update {
            pollAnswer {
                pollId(messageLog[chatPoll]!!.poll!!.id)
                option(option - 1)
                user(person.toUser())
            }
        }

        dispatcher.scenario {
            index(index)
            update(update)
        }
    }

    private fun Command.Poll.stub(index: Int) {
        val message = message { poll() }
        messageLog += chatPoll to message
        clock.set(time)

        dispatcher.scenario {
            index(index)
            poll(message)
        }
    }

    private fun Command.PinMessage.stub(index: Int) {
        dispatcher.scenario {
            index(index)
            pin()
        }
    }

    private fun LoggedRequest.toText(): String {
        return when {
            url.contains(sendMessage) -> bodyAsString.toJson().findPath("text").asText()
            url.contains(sendPoll) -> {
                val json = bodyAsString.toJson()
                val question = json.findPath("question").asText()
                val options = json.findPath("options")
                        .mapIndexed { index, option -> "${index + 1}. '${option.findPath("text").asText()}'" }
                        .joinToString("\n")

                """
                    |$question
                    |$options
                """.trimMargin()
            }

            url.contains(pinChatMessage) -> "pinned"

            else -> "Unsupported message type"
        }
    }
}