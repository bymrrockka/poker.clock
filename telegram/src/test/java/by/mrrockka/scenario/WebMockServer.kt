@file:OptIn(ExperimentalTime::class)

package by.mrrockka.scenario

import by.mrrockka.BotProperties
import by.mrrockka.builder.BuilderDsl
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.chat.unpinChatMessage
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.msg.Message
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
import java.util.concurrent.LinkedBlockingQueue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
private val serde = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

private val scenarioHeader = "Scenario"
private val defaultMessageBody = serde.encodeToString(Response.Success("NOT OK"))
private fun defaultBooleanBody(success: Boolean = true) = if (success) {
    serde.encodeToString(Response.Success(success))
} else {
    serde.encodeToString(
            Response.Failure(
                    errorCode = 401,
                    description = "No mock found",
            ),
    )
}

@Component
class MockDispatcher(
        private val botProps: BotProperties,
        private val mapper: ObjectMapper,
        private val clock: TestClock,
) : Dispatcher() {

    var requests: MutableMap<Int, String> = mutableMapOf()

    @Volatile
    private var scenarios: ArrayDeque<Scenario> = ArrayDeque()
    private val emptyScenario = Scenario.Builder {}.build()

    fun scenario(init: Scenario.Builder.() -> Unit) {
        this.scenarios += Scenario.Builder(init).build()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        var scenario = emptyScenario

        synchronized(scenarios) {
            scenario = when {
                scenarios.isEmpty() -> emptyScenario
                scenarios.first().isNotEmpty() -> scenarios.first()
                else -> {
                    scenarios.removeFirst()
                    if (scenarios.isNotEmpty()) scenarios.first()
                    else emptyScenario
                }
            }

            if (scenario.time != null) {
                clock.set(scenario.time)
            }
        }

        return when (request.url.encodedPath) {
            "${botProps.botpath}/$getUpdates" -> {
                synchronized(scenario.updates) {
                    if (scenario.updates.isNotEmpty())
                        scenario.updates.take()
                    else MockResponse(body = serde.encodeToString(Response.Success(emptyList<Update>())))
                }
            }

            "${botProps.botpath}/$messageMethodName" -> {
                synchronized(scenario.responses) {
                    if (scenario.responses.isNotEmpty()) {
                        val resp = scenario.responses.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to request.toJson().findPath("text").asText()
                        resp
                    } else MockResponse(code = 404, body = defaultMessageBody)
                }
            }

            "${botProps.botpath}/$pollMethodName" -> {
                synchronized(scenario.polls) {
                    if (scenario.polls.isNotEmpty()) {
                        val resp = scenario.polls.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to request.toPollText()
                        resp
                    } else MockResponse(code = 404, body = defaultMessageBody)
                }
            }

            "${botProps.botpath}/$pinChatMethodName" ->
                synchronized(scenario.pins) {
                    if (scenario.pins.isNotEmpty()) {
                        val resp = scenario.pins.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to "pinned"
                        resp
                    } else MockResponse(code = 200, body = defaultBooleanBody(true))
                }

            "${botProps.botpath}/$unpinChatMethodName" ->
                synchronized(scenario.unpins) {
                    if (scenario.unpins.isNotEmpty()) {
                        val resp = scenario.unpins.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to "unpinned"
                        resp
                    } else MockResponse(code = 200, body = defaultBooleanBody(true))
                }

            else -> MockResponse(code = 404, body = defaultBooleanBody(false))
        }
    }

    fun reset() {
        requests.clear()
        scenarios.clear()
    }

    private fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())

    private fun RecordedRequest.toPollText(): String {
        val json = this.toJson()
        val question = json.findPath("question").asText()
        val options = json.findPath("options")
                .mapIndexed { index, option -> "${index + 1}. '${option.findPath("text").asText()}'" }
                .joinToString("\n")

        return """
                |$question
                |$options
            """.trimMargin()
    }

    companion object {
        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val getUpdates = GetUpdatesAction().run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val messageMethodName = message("").run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val pollMethodName = poll("", emptyList()).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val pinChatMethodName = pinChatMessage(-1L).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val unpinChatMethodName = unpinChatMessage(-1L).run { methodName }
    }
}

@Component
class MockServer(
        private val dispatcher: Dispatcher,
) {
    lateinit var server: MockWebServer

    @PostConstruct
    fun init() {
        server = MockWebServer()
        server.dispatcher = dispatcher
        server.start()
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
        val unpins: LinkedBlockingQueue<MockResponse>,
        val time: Instant? = null,
) {

    fun isEmpty(): Boolean {
        return updates.isEmpty() &&
                responses.isEmpty() &&
                polls.isEmpty() &&
                pins.isEmpty() &&
                unpins.isEmpty()
    }

    fun isNotEmpty(): Boolean = !isEmpty()

    @BuilderDsl
    class Builder(init: Builder.() -> Unit) {
        private var index = -1
        private val updates = LinkedBlockingQueue<MockResponse>()
        private val responses = LinkedBlockingQueue<MockResponse>()
        private val polls = LinkedBlockingQueue<MockResponse>()
        private val pins = LinkedBlockingQueue<MockResponse>()
        private val unpins = LinkedBlockingQueue<MockResponse>()
        private var time: Instant? = null

        init {
            init()
        }

        fun index(index: Int) {
            this.index = index
        }

        fun update(update: Update) {
            updates += MockResponse(body = serde.encodeToString(Response.Success(listOf(update))))
        }

        fun message(message: Message) {
            check(index > -1) { "Scenario index should be specified and positive" }
            responses += MockResponse(
                    body = serde.encodeToString(Response.Success(message)),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun poll(message: Message) {
            check(index > -1) { "Scenario index should be specified and positive" }
            polls += MockResponse(
                    body = serde.encodeToString(Response.Success(message)),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun pin() {
            check(index > -1) { "Scenario index should be specified and positive" }
            pins += MockResponse(
                    body = defaultBooleanBody(),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun unpin() {
            check(index > -1) { "Scenario index should be specified and positive" }
            unpins += MockResponse(
                    body = defaultBooleanBody(),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun time(time: Instant) {
            this.time = time
        }

        fun build(): Scenario {
            return Scenario(
                    updates = updates,
                    responses = responses,
                    polls = polls,
                    pins = pins,
                    unpins = unpins,
                    time = time,
            )
        }
    }
}
