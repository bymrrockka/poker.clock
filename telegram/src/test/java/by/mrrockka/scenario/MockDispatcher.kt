package by.mrrockka.scenario

import by.mrrockka.BotProperties
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.RecordedRequest
import okhttp3.Headers.Companion.headersOf
import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue

@OptIn(ExperimentalSerializationApi::class)
private val serde = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

@Component
class MockDispatcher(
        private val botProps: BotProperties,
        private val mapper: ObjectMapper,
) : Dispatcher() {

    private val updateQueue = LinkedBlockingQueue<MockResponse>()
    private val responseQueue = LinkedBlockingQueue<MockResponse>()
    private val pollQueue = LinkedBlockingQueue<MockResponse>()
    private val scenarioHeader = "Scenario"
    var requests: Map<Int, String> = mutableMapOf()

    fun update(update: Update) {
        updateQueue += MockResponse(body = serde.encodeToString(Response.Success(listOf(update))))
    }

    fun response(message: String = "TEST OK", scenarioIndex: Int = 0) {
        responseQueue += MockResponse(body = serde.encodeToString(Response.Success(message)), headers = headersOf(scenarioHeader, "$scenarioIndex"))
    }

    fun poll(poll: Poll) {
        pollQueue += MockResponse(body = serde.encodeToString(Response.Success(poll)))
    }

//    fun pin() {
//        pinQueue += MockResponse(body = serde.encodeToString(Response.Success(poll)))
//    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.url.encodedPath) {
            "${botProps.botpath}/$getUpdates" -> updateQueue.take()
            "${botProps.botpath}/$sendMessage" -> {
                val resp = responseQueue.take()
                val scenarioIndex = resp.headers[scenarioHeader]?.toInt() ?: -1
                requests += scenarioIndex to request.toJson().findPath("text").asText()
                resp
            }

            "${botProps.botpath}/$sendPoll" -> pollQueue.take()
            "${botProps.botpath}/$pinChatMessage" -> responseQueue.take()

            else -> MockResponse(code = 404)
        }
    }

    private fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())


    companion object {
        @JvmStatic
        private val okResponse = Response.Success("TEST OK")

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