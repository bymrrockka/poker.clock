package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.config.TelegramPSQLExtension
import by.mrrockka.config.TestBotConfig
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.MessageCreator.randomMessageId
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
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
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
@ActiveProfiles(profiles = ["repository", "exception-handler"])
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
                    .distinct()
                    .forEach { mention -> entity(mention, EntityType.MENTION) }
        }

        fun entity(value: String, type: String) {
            when (type) {
                EntityType.BOTCOMMAND -> entities += MessageEntityCreator.apiCommand(message, value)
                EntityType.MENTION -> entities += MessageEntityCreator.apiMention(message, value)
            }
        }
    }

    class GivenSpecification {
        var commands: List<Command> = mutableListOf()

        fun command(init: Command.() -> Unit) {
            this.commands += Command(init)
        }
    }

    class ThenSpecification() {
        var expects: List<Expect<*>> = mutableListOf()

        fun <T : PartialBotApiMethod<*>> expect(expect: Expect<T>.() -> Unit) {
            this.expects += Expect<T>().apply(expect)
        }
    }

    class Expect<T : PartialBotApiMethod<*>> {
        lateinit var url: String
        var result: T? = null

        fun url(url: String) {
            this.url = url
        }

        fun result(result: T) {
            this.result = result
        }
    }

    fun List<Command>.updateReceived() = mapIndexed { index, command ->
        UpdateCreator.update {
            this.message = MessageCreator.message { message ->
                message.messageId = randomMessageId()
                message.text = command.message
                message.entities = command.entities
            }
        }
    }.toList().also { updates ->
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
            toState = "expect0"
        }
    }


    fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

    infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): GivenSpecification = this.apply(block)

    infix fun GivenSpecification.Then(block: ThenSpecification.() -> Unit) = ThenSpecification()
            .apply(block)
            .also { thenExecute(it) }
            .let { thenAssert(it) }

    private fun thenExecute(thenSpec: ThenSpecification) {
        thenSpec.expects.forEachIndexed { index, expect ->
            wireMock.post {
                url equalTo "/${botProps.token}/${expect.result!!.method}"
                priority = 1
                whenState = "expect$index"
            } returnsJson {
                body = """
            {
                "ok": "true",
                "result": ${expect.result!!.toJsonString()}
            }
            """
            } and {
                toState = "expect${index + 1}"
            }
        }
    }

    private fun thenAssert(thenSpec: ThenSpecification) {
        thenSpec.expects.forEach { expect ->
            await.atMost(Duration.ofSeconds(3)).untilAsserted {
                wireMock.verify {
                    url equalTo "/${botProps.token}/${expect.result!!.method}"
                    method = RequestMethod.POST
                    exactly = 1
                    when (val resp = expect.result!!) {
                        is SendMessage -> body contains "text" equalTo resp.text
                    }
                }
            }
        }
    }

}