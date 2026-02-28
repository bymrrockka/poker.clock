@file:OptIn(ExperimentalTime::class)

package by.mrrockka.scenario

import by.mrrockka.BotProperties
import by.mrrockka.builder.BuilderDsl
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.chat.unpinChatMessage
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.deleteMessages
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
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
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
    @Volatile
    var requests: MutableMap<Int, String> = mutableMapOf()

    @Volatile
    private var scenarios: ArrayDeque<Scenario> = ArrayDeque()

    fun scenario(init: Scenario.Builder.() -> Unit) {
        this.scenarios += Scenario.Builder(init).build()
    }

    private fun ArrayDeque<Scenario>.retrieve(): Scenario {
        return when {
            isEmpty() -> empty
            first().isNotEmpty() -> scenarios.first()
            else -> {
                removeFirst()
                retrieve()
            }
        }
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return synchronized(empty) {
            val scenario = scenarios.retrieve()
            if (scenario.time != null) {
                clock.set(scenario.time)
            }

            when (request.url.encodedPath) {
                "${botProps.botpath}/$getUpdates" -> {
                    if (scenario.updates.isNotEmpty())
                        scenario.updates.take()
                    else MockResponse(body = serde.encodeToString(Response.Success(emptyList<Update>())))
                }

                "${botProps.botpath}/$sendMessage" -> {
                    if (scenario.responses.isNotEmpty()) {
                        val resp = scenario.responses.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to request.toJson().findPath("text").asString()
                        resp
                    } else MockResponse(code = 404, body = defaultMessageBody)
                }

                "${botProps.botpath}/$sendPoll" -> {
                    if (scenario.polls.isNotEmpty()) {
                        val resp = scenario.polls.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to request.toPollText()
                        resp
                    } else MockResponse(code = 404, body = defaultMessageBody)
                }

                "${botProps.botpath}/$pinMessage" ->
                    if (scenario.pins.isNotEmpty()) {
                        val resp = scenario.pins.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to "pinned"
                        resp
                    } else MockResponse(code = 200, body = defaultBooleanBody(true))

                "${botProps.botpath}/$unpinMessage" ->
                    if (scenario.unpins.isNotEmpty()) {
                        val resp = scenario.unpins.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to "unpinned"
                        resp
                    } else MockResponse(code = 200, body = defaultBooleanBody(true))

                "${botProps.botpath}/$deleteMessages" ->
                    if (scenario.toDelete.isNotEmpty()) {
                        val resp = scenario.toDelete.take()
                        val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                        requests += scenarioIndex to "deleted"
                        resp
                    } else MockResponse(code = 200, body = defaultBooleanBody(true))

                else -> MockResponse(code = 404, body = defaultBooleanBody(false))
            }
        }
    }

    fun reset() {
        synchronized(empty) {
            requests.clear()
            scenarios.clear()
        }
    }

    private fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())

    private fun RecordedRequest.toPollText(): String {
        val json = this.toJson()
        val question = json.findPath("question").asString()
        val options = json.findPath("options")
            .mapIndexed { index, option -> "${index + 1}. '${option.findPath("text").asString()}'" }
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
        private val sendMessage = message("").run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val sendPoll = poll("", emptyList()).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val pinMessage = pinChatMessage(-1L).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val unpinMessage = unpinChatMessage(-1L).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val deleteMessages = deleteMessages(emptyList()).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val empty = Scenario.Builder {}.build()
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
    val toDelete: LinkedBlockingQueue<MockResponse>,
    val time: Instant? = null,
) {

    fun isEmpty(): Boolean {
        return updates.isEmpty() &&
                responses.isEmpty() &&
                polls.isEmpty() &&
                pins.isEmpty() &&
                unpins.isEmpty() &&
                toDelete.isEmpty()
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
        private val toDelete = LinkedBlockingQueue<MockResponse>()
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

        fun delete() {
            check(index > -1) { "Scenario index should be specified and positive" }
            toDelete += MockResponse(
                body = defaultBooleanBody(),
                headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun build(): Scenario {
            return Scenario(
                updates = updates,
                responses = responses,
                polls = polls,
                pins = pins,
                unpins = unpins,
                toDelete = toDelete,
                time = time,
            )
        }
    }
}
