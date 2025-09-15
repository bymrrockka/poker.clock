package by.mrrockka.scenario

import by.mrrockka.Command
import by.mrrockka.GivenSpecification
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.WhenSpecification
import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.builder.update
import by.mrrockka.builder.user
import by.mrrockka.extension.TelegramPSQLExtension
import by.mrrockka.extension.TelegramWiremockContainer
import by.mrrockka.extension.TelegramWiremockExtension
import by.mrrockka.extension.TextApproverExtension
import by.mrrockka.reset
import by.mrrockka.scenario.config.TestBotConfig
import by.mrrockka.scenario.config.TestClock
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.Metadata.metadata
import com.github.tomakehurst.wiremock.verification.LoggedRequest
import com.marcinziolo.kotlin.wiremock.and
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.marcinziolo.kotlin.wiremock.verify
import com.oneeyedmen.okeydoke.Approver
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.botactions.setMyCommands
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import kotlin.time.ExperimentalTime

@ExtendWith(value = [TelegramPSQLExtension::class, TelegramWiremockExtension::class, TextApproverExtension::class])
@ActiveProfiles(profiles = ["scenario", "production"])
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {

    private val randoms = telegramRandoms("scenario")
    private val chatid = randoms.chatid()
    private val user = user(randoms)
    private val messageLog = mutableMapOf<String, Long>()

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    @Autowired
    lateinit var bot: TelegramBot

    @Autowired
    @OptIn(ExperimentalTime::class)
    lateinit var clock: TestClock

    fun Any.toJsonString(): String = mapper.writeValueAsString(this)
    fun String.toJson(): JsonNode = mapper.readTree(this)

    @BeforeEach
    fun setUp() {
        wireMock.resetToDefaultMappings()
    }

    @AfterEach
    fun after() {
        randoms.reset()
        bot.update.stopListener()
        wireMock.verify {
            url equalTo "${botProps.botpath}/${getUpdates}"
            atLeast = 1
        }
//                wireMock.verify {
//                    url equalTo "${botpath}/${SetMyCommands.PATH}"
//                    atMost = 1
//                }
//                wireMock.verify {
//                    url equalTo "${botpath}/${DeleteWebhook.PATH}"
//                    atMost = 1
//                }
    }

    companion object {
        lateinit var wireMock: WireMock
        const val METADATA_ATTR = "scenario"
        val testResponse = Response.Success("TEST OK")

        @OptIn(KtGramInternal::class)
        val getUpdates = GetUpdatesAction().run { methodName }

        @OptIn(KtGramInternal::class)
        val sendMessage = sendMessage("").run { methodName }

        @OptIn(KtGramInternal::class)
        val sendPoll = poll("", emptyList()).run { methodName }

        @OptIn(KtGramInternal::class)
        val setMyCommands = setMyCommands { }.run { methodName }

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
            }
        }

        GlobalScope.launch {
            bot.handleUpdates()
        }
    }

    infix fun WhenSpecification.ThenApprove(approver: Approver) {
        await.atMost(Duration.ofSeconds(2))
                .until {
                    val stubs = wireMock.serveEvents
                            .filter { it.stubMapping.metadata != null && it.stubMapping.metadata.contains(METADATA_ATTR) }
                            .associate { it.stubMapping.metadata.getInt(METADATA_ATTR) to it.request.asText() }

                    if (stubs.size == commands.size) {
                        commands
                                .mapIndexed { index, command ->
                                    when (command) {
                                        is Command.Message ->
                                            """
                                               |******************************
                                               |-> Request
                                               |${command.message}
                                               |
                                               |-> Response
                                               |${stubs[index] ?: "No message"}                   
                                               """.trimMargin()

                                        is Command.Poll ->
                                            """
                                               |******************************
                                               |-> Received
                                               |${stubs[index] ?: "No message"}                   
                                               """.trimMargin()

                                        else -> "Command type is not found"
                                    }
                                }.joinToString("\n\n")
                                .also { approver.assertApproved(it.trim()) }
                        true
                    } else false
                }
    }

    private fun Command.Message.stub(index: Int, seed: String, chatId: Long) {
        val update = update {
            message {
                text(this@stub.message)
                chatId(chatId)
                from(this@AbstractScenarioTest.user)
                if (replyTo != null && messageLog[replyTo] != null) {
                    replyTo {
                        chatId(chatId)
                        id(messageLog[replyTo]!!)
                    }
                }
            }
        }

        messageLog += botcommand to update.message!!.messageId
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
            body = serde.encodeToString(testResponse)
        } and {
            toState = "${seed}${index + 1}"
        }
    }

    private fun Command.Poll.stub(index: Int, seed: String) {
        //mocks telegram response when bot sends message
        wireMock.post {
            url equalTo "${botProps.botpath}/${sendPoll}"
            whenState = "${seed}${index}"
            withBuilder { withMetadata(metadata().attr("scenario", index)) }
        } returnsJson {
            body = serde.encodeToString(testResponse)
        } and {
            toState = "${seed}${index + 1}"
        }
    }

    private fun LoggedRequest.asText(): String {
        return when {
            url.contains(sendMessage) -> bodyAsString.toJson().findPath("text").asText()
            url.contains(sendPoll) -> {
                val json = bodyAsString.toJson()
                val question = json.findPath("question").asText()
                val options = json.findPath("options")
                        .mapIndexed { index, option -> "${index + 1}. '${option.findPath("text").asText()}'" }
                        .joinToString("\n")

                return """
                    |$question
                    |$options
                """.trimMargin()
            }

            else -> "Unsupported message type"
        }
    }
}