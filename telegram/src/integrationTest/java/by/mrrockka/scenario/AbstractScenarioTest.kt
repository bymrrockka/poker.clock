package by.mrrockka.scenario

import by.mrrockka.Random
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.config.TestBotConfig
import by.mrrockka.creator.ChatCreator
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.UpdateCreator
import by.mrrockka.domain.GameType
import by.mrrockka.extension.TelegramPSQLExtension
import by.mrrockka.extension.TelegramWiremockContainer
import by.mrrockka.extension.TelegramWiremockExtension
import by.mrrockka.extension.TextApproverExtension
import by.mrrockka.scenario.UserCommand.Companion.createGame
import by.mrrockka.scenario.UserCommand.Companion.gameResponse
import by.mrrockka.scenario.UserCommand.Companion.gameStats
import by.mrrockka.scenario.UserCommand.Companion.gameStatsResponse
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
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Duration

@ExtendWith(value = [TelegramPSQLExtension::class, TelegramWiremockExtension::class, TextApproverExtension::class])
@ActiveProfiles(profiles = ["integration", "repository"])
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {

    val randoms = telegramRandoms("scenario")
    val chatid = randoms.chatid()

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    fun Any.toJsonString(): String = mapper.writeValueAsString(this)
    fun String.toJson(): JsonNode = mapper.readTree(this)

    @BeforeEach
    fun setUp() {
        wireMock.resetToDefaultMappings()
    }

    companion object {
        lateinit var wireMock: WireMock
        const val METADATA_ATTR = "scenario"
        private val testResponseBuilder = SendMessage.builder().text("TEST OK")

        @JvmStatic
        @BeforeAll
        fun beforeAll(): Unit {
            wireMock = WireMock(TelegramWiremockContainer.port)
        }

        @JvmStatic
        @AfterAll
        fun afterAll(): Unit {
            await.atMost(Duration.ofSeconds(1)).untilAsserted {
                wireMock.verify {
                    url equalTo "/bottoken/${SetMyCommands.PATH}"
                    atMost = 1
                }
                wireMock.verify {
                    url equalTo "/bottoken/${DeleteWebhook.PATH}"
                    atMost = 1
                }
                wireMock.verify {
                    url equalTo "/bottoken/${GetUpdates.PATH}"
                    atLeast = 1
                }
            }
        }
    }

    fun GivenSpecification.updatesReceived(chatId: Long = chatid) {
        this.commands.mapIndexed { index, command ->
            UpdateCreator.update {
                this.message = MessageCreator.message { message ->
                    message.messageId = Random.messageId()
                    message.text = command.message
                    message.chat = ChatCreator.chat(chatId)
                    message.entities = command.entities
                }
            }
        }.also { updates ->
            wireMock.post {
                url equalTo "/${botProps.token}/${GetUpdates.PATH}"
            } returnsJson {
                body = """
            {
                "ok": "true",
                "result": ${updates.toJsonString()}
            }
            """
            } and {
                toState = "updates"
            }

            wireMock.post {
                url equalTo "/${botProps.token}/${GetUpdates.PATH}"
                whenState = "updates"
            } returnsJson {
                body = """
            {
                "ok": "true",
                "result": ${UpdateCreator.emptyList().toJsonString()}
            }
            """
            } and {
                toState = "${scenarioSeed}0"
            }
        }
    }

    infix fun WhenSpecification.Then(block: ThenSpecification.() -> Unit) = ThenSpecification(this.scenarioSeed)
            .apply(block)
            .also { thenExecute(it) }
            .also { thenAssert(it) }
            .run { wireMock.resetScenarios() }

    infix fun WhenSpecification.ThenApprove(approver: Approver) = this
            .also {
                it.commands?.mapIndexed { index, command ->
                    wireMock.post {
                        url equalTo "/${botProps.token}/${SendMessage().method}"
                        whenState = "${scenarioSeed}$index"
                        withBuilder { withMetadata(metadata().attr("scenario", index)) }
                    } returnsJson {
                        body = """
                            {
                                "ok": "true",
                                "result": ${testResponseBuilder.chatId(chatid).build().toJsonString()}
                            }
                            """
                    } and {
                        toState = "${scenarioSeed}${index + 1}"
                    }
                }
            }
            .also {
                await.atMost(Duration.ofSeconds(1))
                        .until {
                            check(this.commands != null) { "Commands should be specified" }
                            val stubs = wireMock.getServeEvents()
                                    .filter { it.stubMapping.metadata != null && it.stubMapping.metadata.contains(METADATA_ATTR) }
                                    .associate { it.stubMapping.metadata.getInt(METADATA_ATTR) to it.request.bodyAsString.toJson().findPath("text").asText() }
                            assertThat(stubs).hasSize(commands.size)

                            if (stubs.size == commands.size) {
                                commands.mapIndexed { index, command ->
                                    """
                                       |### Command $index ###
                                       |${command.message}
                                       |--- Response ---
                                       |${stubs[index] ?: "No message"}                   
                                       """.trimMargin()
                                }
                                        .joinToString("\n\n")
                                        .also { approver.assertApproved(it.trimIndent()) }
                                true
                            } else false
                        }
            }

    private fun thenExecute(thenSpec: ThenSpecification) {
        thenSpec.expects.forEachIndexed { index, expect ->
            wireMock.post {
                url equalTo "/${botProps.token}/${expect.result.method}"
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

    private fun thenAssert(thenSpec: ThenSpecification) {
        thenSpec.expects.forEach { expect ->
            await.atMost(Duration.ofSeconds(1)).untilAsserted {
                wireMock.verify {
                    url equalTo "/${botProps.token}/${expect.result.method}"
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