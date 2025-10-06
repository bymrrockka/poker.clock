package by.mrrockka.scenario

import by.mrrockka.BotProperties
import by.mrrockka.builder.BuilderDsl
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.sendMessage
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.poll.Poll
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import okhttp3.Headers.Companion.headersOf
import okhttp3.internal.closeQuietly
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.util.concurrent.LinkedBlockingQueue

@OptIn(ExperimentalSerializationApi::class)
private val serde = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

internal val scenarioHeader = "Scenario"

@Component
class MockDispatcher(
        private val botProps: BotProperties,
        private val mapper: ObjectMapper,
) : Dispatcher() {

    var requests: MutableMap<Int, String> = mutableMapOf()
    var scenarios: ArrayDeque<Scenario> = ArrayDeque()

    private val emptyScenario = Scenario.Builder {}.build()

    fun scenario(init: Scenario.Builder.() -> Unit) {
        this.scenarios += Scenario.Builder(init).build()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val scenario = when {
            scenarios.isEmpty() -> emptyScenario
            scenarios.first().isNotEmpty() -> scenarios.first()
            else -> {
                scenarios.removeFirst()
                if (scenarios.isNotEmpty()) scenarios.first()
                else emptyScenario
            }
        }

        return when (request.url.encodedPath) {
            "${botProps.botpath}/$getUpdates" -> {
                synchronized(scenario) {
                    if (scenario.updates.isNotEmpty())
                        scenario.updates.take()
                    else MockResponse(body = serde.encodeToString(Response.Success(emptyList<Update>())))
                }
            }

            "${botProps.botpath}/$sendMessage" -> {
                synchronized(scenario) {
                    if (scenario.responses.isNotEmpty()) {
                        val resp = scenario.responses.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to request.toJson().findPath("text").asText()
                        resp
                    } else MockResponse(code = 404, body = "No messages found")
                }
            }

            "${botProps.botpath}/$sendPoll" -> {
                synchronized(scenario) {
                    if (scenario.polls.isNotEmpty())
                        scenario.polls.take()
                    else MockResponse(code = 404, body = "No polls found")
                }
            }

            "${botProps.botpath}/$pinChatMessage" ->
                synchronized(scenario) {
                    if (scenario.pins.isNotEmpty())
                        scenario.pins.take()
                    else MockResponse(code = 404, body = "No pins found")
                }

            else -> MockResponse(code = 404, body = "No mocks found")
        }
    }

    fun reset() {
        requests.clear()
        scenarios.clear()
    }

    private fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())

    companion object {
        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val getUpdates = GetUpdatesAction().run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val sendMessage = sendMessage("").run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val sendPoll = poll("", emptyList()).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val pinChatMessage = pinChatMessage(-1L).run { methodName }
    }
}

@Component
class TgMockServer(
        private val dispatcher: Dispatcher,
) {
    lateinit var server: MockWebServer

    @PostConstruct
    fun init() {
        server = MockWebServer()
        server.dispatcher = dispatcher
        server.start(InetAddress.getByName("localhost"), 45678)
    }

    @PreDestroy
    fun destroy() {
        server.closeQuietly()
    }
}

data class Scenario(
        val updates: LinkedBlockingQueue<MockResponse>,
        val responses: LinkedBlockingQueue<MockResponse>,
        val polls: LinkedBlockingQueue<MockResponse>,
        val pins: LinkedBlockingQueue<MockResponse>,
) {

    fun isEmpty(): Boolean {
        return updates.isEmpty() && responses.isEmpty() && polls.isEmpty() && pins.isEmpty()
    }

    fun isNotEmpty(): Boolean = !isEmpty()

    @BuilderDsl
    class Builder(init: Builder.() -> Unit) {
        private var index = -1
        private val updates = LinkedBlockingQueue<MockResponse>()
        private val responses = LinkedBlockingQueue<MockResponse>()
        private val polls = LinkedBlockingQueue<MockResponse>()
        private val pins = LinkedBlockingQueue<MockResponse>()

        init {
            init()
        }

        fun index(index: Int) {
            this.index = index
        }

        fun update(update: Update) {
            updates += MockResponse(body = serde.encodeToString(Response.Success(listOf(update))))
        }

        fun response(message: String = "TEST OK") {
            check(index > -1) { "Scenario index should be specified and positive" }
            responses += MockResponse(
                    body = serde.encodeToString(Response.Success(message)),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun poll(poll: Poll) {
            polls += MockResponse(body = serde.encodeToString(Response.Success(poll)))
        }

        fun build(): Scenario {
            return Scenario(
                    updates = updates,
                    responses = responses,
                    polls = polls,
                    pins = pins,
            )
        }
    }
}
