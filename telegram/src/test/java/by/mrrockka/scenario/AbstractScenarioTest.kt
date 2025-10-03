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
import com.github.tomakehurst.wiremock.common.Metadata.metadata
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import com.marcinziolo.kotlin.wiremock.and
import com.marcinziolo.kotlin.wiremock.contains
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.marcinziolo.kotlin.wiremock.verify
import com.oneeyedmen.okeydoke.Approver
import eu.vendeli.tgbot.TelegramBot
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {

    private val randoms = telegramRandoms("scenario")
    private val chatid = randoms.chatid()
    private val user = user(randoms)
    private val messageLog = mutableMapOf<String, Message>()

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: BotProperties

    @Autowired
    lateinit var bot: TelegramBot

    @Autowired
    lateinit var clock: TestClock

    private fun String.toJson(): JsonNode = mapper.readTree(this)

    @BeforeEach
    fun setUp() {
        wireMock.resetToDefaultMappings()
    }

    @AfterEach
    fun after() {
        coreRandoms.reset()
        telegramRandoms.reset()
        bot.update.stopListener()
        wireMock.verify {
            url equalTo "${botProps.botpath}/${getUpdates}"
            atLeast = 1
        }
    }

    companion object {
        lateinit var wireMock: WireMock
        const val METADATA_ATTR = "scenario"
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
        this.commands.forEachIndexed { index, command ->
            when (command) {
                is Command.Message -> command.stub(index, scenarioSeed, chatId)
                is Command.Poll -> command.stub(index, scenarioSeed)
                is Command.PollAnswer -> command.stub(index, scenarioSeed)
                is Command.PinMessage -> command.stub(index, scenarioSeed)
            }
        }

        //placed here bot init as it falling with serialization exception if run in parallel during stub config
        GlobalScope.launch {
            bot.handleUpdates()
        }
    }

    infix fun WhenSpecification.ThenApproveWith(approver: Approver) {
        try {
            await atMost Duration.ofSeconds(3) untilCallTo {
                requests()
            } has {
                size == commands.filter { it !is Command.PollAnswer }.size
            }
        } catch (ex: Exception) {
            logger.error { "Await timeout failed" }
        }

        commands.toText(requests())
                .also { approver.assertApproved(it.trim()) }
    }

    private fun requests(): Map<Int, String> = wireMock.serveEvents
            .filter { it.stubMapping.metadata != null && it.stubMapping.metadata.contains(METADATA_ATTR) }
            .associate { it.stubMapping.metadata.getInt(METADATA_ATTR) to it.request.toText() }

    private fun List<Command>.toText(stubs: Map<Int, String>): String {
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
                   |${stubs[index] ?: errorMessage} 
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
                   |${stubs[index] ?: errorMessage}
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
                   |${command.toText()} ${stubs[index] ?: errorMessage}
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

    private fun Command.Message.stub(index: Int, seed: String, chatId: Long) {
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
        //mocks user command from telegram
        wireMock.post {
            url equalTo "${botProps.botpath}/${getUpdates}"
            whenState = "${seed}$index"
        } returnsJson {
            body = serde.encodeToString(Response.Success(listOf(update)))
        } and {
            toState = "${seed}${index}-completed"
        }
        //mocks telegram response when bot sends message
        wireMock.post {
            url equalTo "${botProps.botpath}/${sendMessage}"
            whenState = "${seed}$index-completed"
            withBuilder { withMetadata(metadata().attr("scenario", index)) }
        } returnsJson {
            body = serde.encodeToString(mockMessageResponse)
        } and {
            toState = "${seed}${index + 1}"
        }
    }

    private fun Command.PollAnswer.stub(index: Int, seed: String) {
        val update = update {
            pollAnswer {
                pollId(messageLog[chatPoll]!!.poll!!.id)
                option(option - 1)
                user(person.toUser())
            }
        }

        //mocks user command from telegram
        wireMock.post {
            url equalTo "${botProps.botpath}/${getUpdates}"
            whenState = "${seed}$index"
        } returnsJson {
            body = serde.encodeToString(Response.Success(listOf(update)))
        } and {
            toState = "${seed}${index + 1}"
        }
    }

    private fun Command.Poll.stub(index: Int, seed: String) {
        val message = message { poll() }
        messageLog += chatPoll to message
        clock.set(time)
        //mocks telegram response when bot sends message
        wireMock.post {
            url equalTo "${botProps.botpath}/${sendPoll}"
            whenState = "${seed}${index}"
            withBuilder { withMetadata(metadata().attr("scenario", index)) }
        } returnsJson {
            body = serde.encodeToString(Response.Success(message))
        } and {
            toState = "${seed}${index + 1}"
        }
    }

    private fun Command.PinMessage.stub(index: Int, seed: String) {
        //mocks telegram response when bot sends message
        wireMock.post {
            url equalTo "${botProps.botpath}/${pinChatMessage}"
            whenState = "${seed}${index}"
            withBuilder { withMetadata(metadata().attr("scenario", index)) }
            body contains "message_id" equalTo (messageLog[message]?.messageId ?: error("$message not found in log"))
        } returnsJson {
            body = serde.encodeToString(mockMessageResponse)
        } and {
            toState = "${seed}${index + 1}"
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