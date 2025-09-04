package by.mrrockka.scenario

import by.mrrockka.Given
import by.mrrockka.GivenSpecification
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.ThenSpecification
import by.mrrockka.When
import by.mrrockka.WhenSpecification
import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.builder.update
import by.mrrockka.domain.GameType
import by.mrrockka.extension.TelegramPSQLExtension
import by.mrrockka.extension.TelegramWiremockContainer
import by.mrrockka.extension.TelegramWiremockExtension
import by.mrrockka.extension.TextApproverExtension
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.gameResponse
import by.mrrockka.scenario.UserCommand.Companion.gameStats
import by.mrrockka.scenario.UserCommand.Companion.gameStatsResponse
import by.mrrockka.scenario.config.TestBotConfig
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.Metadata.metadata
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.marcinziolo.kotlin.wiremock.and
import com.marcinziolo.kotlin.wiremock.contains
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.marcinziolo.kotlin.wiremock.verify
import com.oneeyedmen.okeydoke.Approver
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Duration

@ExtendWith(value = [TelegramPSQLExtension::class, TelegramWiremockExtension::class, TextApproverExtension::class])
@ActiveProfiles(profiles = ["scenario"])
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {

    val randoms = telegramRandoms("scenario")
    val chatid = randoms.chatid()

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    @Autowired
    lateinit var bot: TelegramBot

    fun Any.toJsonString(): String = mapper.writeValueAsString(this)
    fun String.toJson(): JsonNode = mapper.readTree(this)

    @BeforeEach
    fun setUp() {
        wireMock.resetToDefaultMappings()
    }

    @AfterEach
    fun after() {
        bot.update.stopListener()
    }

    companion object {
        lateinit var wireMock: WireMock
        const val METADATA_ATTR = "scenario"
        val testResponse = Response.Success("TEST OK")
        const val getUpdates = "getUpdates"
        const val sendMessage = "sendMessage"

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            wireMock = WireMock(TelegramWiremockContainer.port)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            val botpath = "/bottoken"
//            await.atMost(Duration.ofSeconds(1)).untilAsserted {
//                wireMock.verify {
//                    url equalTo "${botpath}/${SetMyCommands.PATH}"
//                    atMost = 1
//                }
//                wireMock.verify {
//                    url equalTo "${botpath}/${DeleteWebhook.PATH}"
//                    atMost = 1
//                }
                wireMock.verify {
                    url equalTo "${botpath}/${getUpdates}"
                    atLeast = 1
                }
//            }
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

        this.commands.map { command ->
            update {
                message {
                    text(command.message)
                    chatId(chatId)
                }
            }
        }.forEachIndexed { index, update ->
            //mocks user command from telegram
            wireMock.post {
                url equalTo "${botProps.botpath}/${getUpdates}"
                whenState = "${scenarioSeed}$index"
            } returnsJson {
                body = serde.encodeToString(Response.Success(listOf(update)))
            } and {
                toState = "${scenarioSeed}${index}-completed"
            }

            //mocks telegram response when bot sends message
            wireMock.post {
                url equalTo "${botProps.botpath}/${sendMessage}"
                whenState = "${scenarioSeed}$index-completed"
                withBuilder { withMetadata(metadata().attr("scenario", index)) }
            } returnsJson {
                body = serde.encodeToString(testResponse)
            } and {
                toState = "${scenarioSeed}${index + 1}"
            }
        }

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

        GlobalScope.launch {
            launch {
                bot.handleUpdates()
            }
        }

    }

    @Deprecated(message = "use approve method instead", replaceWith = ReplaceWith("thenApprove"))
    infix fun WhenSpecification.Then(block: ThenSpecification.() -> Unit) = ThenSpecification(this.scenarioSeed)
            .apply(block)
            .also { thenExecute(it) }
            .also { thenAssert(it) }
            .run { wireMock.resetScenarios() }

    infix fun WhenSpecification.ThenApprove(approver: Approver) {
        await.atMost(Duration.ofSeconds(1))
                .until {
                    val stubs = wireMock.getServeEvents()
                            .filter { it.stubMapping.metadata != null && it.stubMapping.metadata.contains(METADATA_ATTR) }
                            .associate { it.stubMapping.metadata.getInt(METADATA_ATTR) to it.request.bodyAsString.toJson().findPath("text").asText() }

                    if (stubs.size == commands.size) {
                        commands.mapIndexed { index, command ->
                            """
                                       |******************************
                                       |-> Request
                                       |${command.message}
                                       |
                                       |-> Response
                                       |${stubs[index] ?: "No message"}                   
                                       """.trimMargin()
                        }
                                .joinToString("\n\n")
                                .also { approver.assertApproved(it.trimIndent()) }
                        true
                    } else false
                }
    }

    @Deprecated(message = "use approve method instead", replaceWith = ReplaceWith("thenApprove"))
    private fun thenExecute(thenSpec: ThenSpecification) {
        thenSpec.expects.forEachIndexed { index, expect ->
            wireMock.post {
                url equalTo "${botProps.botpath}/${expect.result.method}"
                whenState = "${thenSpec.scenarioSeed}$index"
                withBuilder { withMetadata(metadata().attr("scenario", index)) }
            } returnsJson {
                body = """
            {
                "ok": "true",
                "result": ${expect.result.toJsonString()}
            }
            """
            } and {
                toState = "${thenSpec.scenarioSeed}${index + 1}"
            }
        }
    }

    @Deprecated(message = "use approve method instead", replaceWith = ReplaceWith("thenApprove"))
    private fun thenAssert(thenSpec: ThenSpecification) {
        thenSpec.expects.forEach { expect ->
            await.atMost(Duration.ofSeconds(1)).untilAsserted {
                wireMock.verify {
                    url equalTo "${botProps.botpath}/${expect.result.method}"
                    method = RequestMethod.POST
                    exactly = 1
                    when (val resp = expect.result) {
                        is SendMessage -> body contains "text" equalTo resp.text
                    }
                }
            }
        }
    }

    fun givenGameCreatedWithChatId(type: GameType, buyin: BigDecimal, players: List<String>) {
        Given {
            command { message(players.createGame(type, buyin)) }
            command { message(gameStats) }
        } When {
            updatesReceived(chatid)
        } Then {
            expect { text<SendMessage>(gameResponse(type)) }
            expect { text<SendMessage>(gameStatsResponse(type, players.size, buyin)) }
        }
    }
}