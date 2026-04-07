@file:OptIn(ExperimentalTime::class)

package by.mrrockka.scenario

import by.mrrockka.BotProperties
import by.mrrockka.builder.BuilderDsl
import by.mrrockka.builder.MessageBuilder
import by.mrrockka.builder.member
import eu.vendeli.tgbot.annotations.internal.KtGramInternal
import eu.vendeli.tgbot.api.botactions.GetUpdatesAction
import eu.vendeli.tgbot.api.chat.getChatMember
import eu.vendeli.tgbot.api.chat.pinChatMessage
import eu.vendeli.tgbot.api.chat.unpinChatMessage
import eu.vendeli.tgbot.api.common.poll
import eu.vendeli.tgbot.api.message.deleteMessages
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.chat.ChatMember
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.msg.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
private val serde = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
    classDiscriminator = "status"
}

private val scenarioHeader = "Scenario"
private val defaultMessageBody = serde.encodeToString(Response.Success(MessageBuilder { text("SKIPPED") }.message()))
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

private val logger = KotlinLogging.logger {}

@Component
class MockDispatcher(
        private val botProps: BotProperties,
        private val mapper: ObjectMapper,
        private val clock: TestClock,
) : Dispatcher() {
    private val delay = 5L
    private var lastPush: Instant? = null
    var requests = mutableMapOf<Int, String>()
    private var interactions = ConcurrentLinkedDeque<Interaction>()
    private var members = ConcurrentHashMap<Long, ChatMember>()

    fun scenario(init: Interaction.Builder.() -> Unit) {
        this.interactions += Interaction.Builder(init).build()
    }

    fun member(member: ChatMember) {
        members += member.user.id to member
    }

    private fun ConcurrentLinkedDeque<Interaction>.retrieve(): Interaction {
        return synchronized(this) {
            val interaction = when {
                isEmpty() -> empty
                first().isNotEmpty() -> first()
                else -> {
                    removeFirst()
                    retrieve()
                }
            }

            if (interaction.time != null) {
                clock.set(interaction.time)
            }
            interaction
        }
    }

    private fun push(block: () -> MockResponse): MockResponse {
        val sync = {
            lastPush = Clock.System.now()
            block()
        }

        if (lastPush == null) {
            return sync()
        }

        while (Clock.System.now().toEpochMilliseconds() - lastPush!!.toEpochMilliseconds() < delay) {
            runBlocking {
                delay(delay)
            }
        }

        return sync()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.url.encodedPath) {
            "${botProps.botpath}/$getUpdates" -> when {
                interactions.retrieve().update.isNotEmpty() -> {
                    val interaction = interactions.retrieve()
                    logger.debug { "Sending updates. Interaction index: ${interaction.index}" }

                    push {
                        requests += interaction.index to "Processed"
                        interaction.update.removeFirst()
                    }
                }

                else -> MockResponse(body = serde.encodeToString(Response.Success(emptyList<Update>())))
            }

            "${botProps.botpath}/$sendMessage" ->
                when {
                    interactions.retrieve().message.isNotEmpty() -> {
                        val interaction = interactions.retrieve()
                        logger.debug { "Send Message request sent. Interaction index: ${interaction.index}" }
                        push {
                            requests += interaction.index to request.toJson().findPath("text").asString()
                            interaction.message.removeFirst()
                        }
                    }

                    else -> {
                        logger.warn { "Send Message request was skipped" }
                        MockResponse(code = 200, body = defaultMessageBody)
                    }
                }

            "${botProps.botpath}/$sendPoll" -> when {
                interactions.retrieve().poll.isNotEmpty() -> {
                    val interaction = interactions.retrieve()
                    requests += interaction.index to request.toPollText()
                    push {
                        logger.debug { "Send Poll request sent. Interaction index: ${interaction.index}" }
                        interaction.poll.removeFirst()
                    }
                }

                else -> {
                    logger.warn { "Send Poll request was skipped" }
                    MockResponse(code = 200, body = defaultMessageBody)
                }
            }

            "${botProps.botpath}/$pinMessage" -> when {
                interactions.retrieve().pin.isNotEmpty() -> {
                    val interaction = interactions.retrieve()
                    logger.debug { "Pin Message request sent. Interaction index: ${interaction.index}" }
                    requests += interaction.index to "pinned"
                    interaction.pin.removeFirst()
                }

                else -> {
                    logger.warn { "Pin Message request was skipped" }
                    MockResponse(code = 200, body = defaultBooleanBody(true))
                }
            }

            "${botProps.botpath}/$unpinMessage" -> when {
                interactions.retrieve().unpin.isNotEmpty() -> {
                    val interaction = interactions.retrieve()
                    requests += interaction.index to "unpinned"
                    logger.debug { "Unpin Message request sent. Interaction index: ${interaction.index}" }
                    interaction.unpin.removeFirst()
                }

                else -> {
                    logger.warn { "Unpin Message request was skipped" }
                    MockResponse(code = 200, body = defaultBooleanBody(true))
                }
            }

            "${botProps.botpath}/$deleteMessages" -> when {
                interactions.retrieve().delete.isNotEmpty() -> {
                    val interaction = interactions.retrieve()
                    logger.debug { "Delete Messages request sent. Interaction index: ${interaction.index}" }
                    requests += interaction.index to "deleted"
                    interaction.delete.removeFirst()
                }

                else -> {
                    logger.warn { "Delete Messages request was skipped" }
                    MockResponse(code = 200, body = defaultBooleanBody(true))
                }
            }

            "${botProps.botpath}/$getMember" -> {
                val member = members[request.userId()]
                when {
                    member != null -> {
                        logger.debug { "Requested member details for ${member.user.username}. Role is ${member.status}." }
                        MockResponse(code = 200, body = serde.encodeToString<Response.Success<ChatMember>>(Response.Success(member)))
                    }

                    else -> {
                        logger.warn { "Member detail request was skipped" }
                        MockResponse(code = 200, body = serde.encodeToString<Response.Success<ChatMember>>(Response.Success(member())))
                    }
                }
            }

            else -> {
                logger.error { "Unknown request type ${request.url.encodedPath}." }
                MockResponse(code = 404, body = defaultBooleanBody(false))
            }
        }
    }

    fun reset() {
        requests.clear()
        interactions.clear()
        members.clear()
    }

    private fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())
    private fun RecordedRequest.userId(): Long = toJson().findPath("user_id").asLong()

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
        private val getMember = getChatMember(1L).run { methodName }

        @JvmStatic
        @OptIn(KtGramInternal::class)
        private val empty = Interaction.Builder {}.build()
    }
}

@Component
class MockServer(
        private val dispatcher: Dispatcher,
) {
    @Volatile
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

data class Interaction(
        val index: Int,
        val update: ArrayDeque<MockResponse>,
        val message: ArrayDeque<MockResponse>,
        val poll: ArrayDeque<MockResponse>,
        val pin: ArrayDeque<MockResponse>,
        val unpin: ArrayDeque<MockResponse>,
        val delete: ArrayDeque<MockResponse>,
        val time: Instant? = null,
) {

    fun isEmpty(): Boolean {
        return update.isEmpty() &&
                message.isEmpty() &&
                poll.isEmpty() &&
                pin.isEmpty() &&
                unpin.isEmpty() &&
                delete.isEmpty()
    }

    fun isNotEmpty(): Boolean = !isEmpty()

    @BuilderDsl
    class Builder(init: Builder.() -> Unit) {
        private var index = -1
        private val update = ArrayDeque<MockResponse>()
        private val message = ArrayDeque<MockResponse>()
        private val poll = ArrayDeque<MockResponse>()
        private val pin = ArrayDeque<MockResponse>()
        private val unpin = ArrayDeque<MockResponse>()
        private val delete = ArrayDeque<MockResponse>()
        private var time: Instant? = null

        init {
            init()
        }

        fun index(index: Int) {
            this.index = index
        }

        fun update(update: Update) {
            check(index > -1) { "Scenario index should be specified and positive" }
            this@Builder.update += MockResponse(body = serde.encodeToString(Response.Success(listOf(update))))
        }

        fun message(message: Message) {
            check(index > -1) { "Scenario index should be specified and positive" }
            this@Builder.message += MockResponse(
                    body = serde.encodeToString(Response.Success(message)),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun poll(message: Message) {
            check(index > -1) { "Scenario index should be specified and positive" }
            poll += MockResponse(
                    body = serde.encodeToString(Response.Success(message)),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun pin() {
            check(index > -1) { "Scenario index should be specified and positive" }
            pin += MockResponse(
                    body = defaultBooleanBody(),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun unpin() {
            check(index > -1) { "Scenario index should be specified and positive" }
            unpin += MockResponse(
                    body = defaultBooleanBody(),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun time(time: Instant) {
            this.time = time
        }

        fun delete() {
            check(index > -1) { "Scenario index should be specified and positive" }
            delete += MockResponse(
                    body = defaultBooleanBody(),
                    headers = headersOf(scenarioHeader, "$index"),
            )
        }

        fun build(): Interaction {
            return Interaction(
                    index = index,
                    update = update,
                    message = message,
                    poll = poll,
                    pin = pin,
                    unpin = unpin,
                    delete = delete,
                    time = time,
            )
        }
    }
}
