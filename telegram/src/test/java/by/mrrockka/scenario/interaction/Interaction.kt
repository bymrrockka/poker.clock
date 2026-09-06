package by.mrrockka.scenario.interaction

import by.mrrockka.builder.BuilderDsl
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.msg.Message
import mockwebserver3.MockResponse
import okhttp3.Headers.Companion.headersOf
import kotlin.time.Instant

private val scenarioHeader = "Scenario"

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
