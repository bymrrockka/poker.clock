@file:OptIn(ExperimentalTime::class)

package by.mrrockka.scenario.interaction

import by.mrrockka.BotProperties
import by.mrrockka.builder.MessageBuilder
import by.mrrockka.builder.member
import by.mrrockka.scenario.TestClock
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
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import okhttp3.internal.closeQuietly
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val defaultMessage = MockResponse(200, body = serde.encodeToString(Response.Success(MessageBuilder { text("SKIPPED") }.message())))
private val logger = KotlinLogging.logger {}

@Component
class MockDispatcher(
        private val botProps: BotProperties,
        private val clock: TestClock,
) : Dispatcher() {
    private val delay = 5L
    private var lastPush: Instant? = null
    var requests = mutableMapOf<Int, String>()
    private var interactions = ConcurrentLinkedDeque<Interaction<*>>()
    private var members = ConcurrentHashMap<Long, ChatMember>()

    fun scenario(init: Interaction.Builder.() -> Unit) {
        this.interactions += Interaction.Builder(init).build()
    }

    fun member(member: ChatMember) {
        members += member.user.id to member
    }

    private fun ConcurrentLinkedDeque<Interaction<*>>.retrieve(): Interaction<*> {
        return synchronized(this) {
            val interaction = when {
                isEmpty() -> empty
                !first().completed -> first()
                else -> {
                    removeFirst()
                    retrieve()
                }
            }

            if (interaction.time != null) {
                clock.set(interaction.time!!)
            }
            interaction
        }
    }

    /**
     * Method should be used to make sure requests are coming in sequential way
     * This one creates a little delay for server event with low resources environment (like github actions pipeline) to give time to process command
     * Use for all requests that require processing time
     */
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
                delay(delay.milliseconds)
            }
        }

        return sync()
    }

    private fun Interaction<*>.process(request: RecordedRequest? = null): MockResponse =
            push {
                val text = when (this) {
                    is Interaction.UpdateResponse -> data.toText()

                    is Interaction.MessageResponse -> {
                        checkNotNull(request) { "Request required" }
                        request.toText(this)
                    }

                    is Interaction.BooleanResponse -> {
                        checkNotNull(request) { "Request required" }
                        request.toText(this)
                    }

                    else -> error("Unknown interaction type")
                }

                requests += index to text
                complete()
                toResponse()
            }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val interaction = interactions.retrieve()
        return when (request.url.encodedPath) {
            "${botProps.botpath}/$getUpdates" -> when (interaction) {
                is Interaction.UpdateResponse -> interaction.process()

                else -> MockResponse(body = serde.encodeToString(Response.Success(emptyList<Update>())))
            }

            "${botProps.botpath}/$sendMessage",
            "${botProps.botpath}/$sendPoll",
                ->
                when (interaction) {
                    is Interaction.MessageResponse -> interaction.process(request)

                    else -> {
                        logger.warn { "${request.url.encodedPath} was skipped" }
                        defaultMessage
                    }
                }

            "${botProps.botpath}/$pinMessage",
            "${botProps.botpath}/$unpinMessage",
            "${botProps.botpath}/$deleteMessages",
                ->
                when (interaction) {
                    is Interaction.BooleanResponse -> interaction.process(request)

                    else -> {
                        logger.warn { "${request.url.encodedPath} was skipped" }
                        Interaction.Empty().toResponse()
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
                MockResponse(
                        code = 404,
                        body = serde.encodeToString(Response.Failure(errorCode = 404, description = "No stub found")),
                )
            }
        }
    }

    fun reset() {
        requests.clear()
        interactions.clear()
        members.clear()
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
        private val empty = Interaction.Empty(0)
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
