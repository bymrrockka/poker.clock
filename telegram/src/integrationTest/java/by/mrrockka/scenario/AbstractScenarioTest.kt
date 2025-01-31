package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.config.TelegramPSQLExtension
import by.mrrockka.config.TestBotConfig
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.MessageEntityCreator
import by.mrrockka.creator.UpdateCreator
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.marcinziolo.kotlin.wiremock.*
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
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import org.wiremock.integrations.testcontainers.WireMockContainer.OFFICIAL_IMAGE_NAME
import java.time.Duration
import kotlin.text.RegexOption.MULTILINE


@ExtendWith(TelegramPSQLExtension::class)
@ActiveProfiles("repository")
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    fun Any.toJsonString(): String = mapper.writeValueAsString(this)


    @BeforeEach
    fun setUp() {
        wireMock.resetToDefaultMappings()
    }

    companion object {

        @Container
        val container = WireMockContainer("$OFFICIAL_IMAGE_NAME:3.10.0")
                .withMappingFromResource("telegram", "wiremock/mappings.json")

        lateinit var wireMock: WireMock

        @JvmStatic
        @BeforeAll
        fun beforeAll(): Unit {
            container.start()
            wireMock = WireMock(container.port)
            System.setProperty("wiremock.server.baseUrl", container.baseUrl)
        }

        @JvmStatic
        @AfterAll
        fun afterAll(): Unit {
            await.atMost(Duration.ofSeconds(3)).untilAsserted {
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


    class Command(init: Command.() -> Unit) {
        lateinit var message: String
        var entities: List<MessageEntity> = mutableListOf()

        init {
            init(this)
        }

        fun message(message: String) {
            this.message = message
            val botCommandRegex = "^(/[\\w_]+)".toRegex(MULTILINE)
            val mentionRegex = "^(@[\\w\\d_]+)".toRegex(MULTILINE)
            val botCommand = botCommandRegex.find(this.message.trimIndent())!!.groups[0]!!.value
            entity(botCommand, EntityType.BOTCOMMAND)

            mentionRegex.findAll(this.message.trimIndent())
                    .map { it.groups[0]!!.value }
                    .forEach { mention -> entity(mention, EntityType.MENTION) }
        }

        fun entity(value: String, type: String) {
            when (type) {
                EntityType.BOTCOMMAND -> entities += MessageEntityCreator.apiCommand(message, value)
                EntityType.MENTION -> entities += MessageEntityCreator.apiMention(message, value)
            }
        }
    }

    class ScenarioSpecification {
        lateinit var command: Command

        fun command(init: (Command) -> Unit) {
            this.command = Command(init)
        }
    }

    class Expected<T> {
        lateinit var url: String
        var result: T? = null
    }

    fun Command.updateReceived() {
        val updates = listOf(
                UpdateCreator.update {
                    this.message = MessageCreator.message { message ->
                        message.messageId = MessageCreator.randomMessageId()
                        message.text = this@updateReceived.message
                        message.entities = entities
                    }
                }
        )
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
        }
    }

    fun Given(block: ScenarioSpecification.() -> Unit): ScenarioSpecification = ScenarioSpecification().apply(block)

    infix fun ScenarioSpecification.When(block: (ScenarioSpecification) -> Unit): ScenarioSpecification = this.apply(block)

    infix fun ScenarioSpecification.Then(block: (Expected<SendMessage>) -> Unit) = Expected<SendMessage>()
            .apply(block)
            .also { thenExecute(it) }
            .let { thenAssert(it) }

    private fun thenExecute(expected: Expected<SendMessage>) {
        wireMock.post {
            url equalTo "/${botProps.token}/${expected.result!!.method}"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${expected.toJsonString()}
            }
            """
        }
    }

    private fun thenAssert(expected: Expected<SendMessage>) {
        await.atMost(Duration.ofSeconds(3)).untilAsserted {
            wireMock.verify {
                url equalTo "/${botProps.token}/${expected.result!!.method}"
                method = RequestMethod.POST
                body contains "text" equalTo expected.result!!.text
            }
        }
    }

}