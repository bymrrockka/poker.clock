package by.mrrockka.scenario.interaction

import by.mrrockka.builder.BuilderDsl
import eu.vendeli.tgbot.types.common.Update
import eu.vendeli.tgbot.types.component.Response
import eu.vendeli.tgbot.types.msg.Message
import mockwebserver3.MockResponse
import kotlin.time.Instant


sealed interface Interaction<T> {
    val index: Int
    val data: T
    val time: Instant?
    var completed: Boolean

    fun complete() {
        completed = true
    }

    fun toResponse(): MockResponse

    abstract class UpdateResponse : Interaction<Update> {
        override fun toResponse(): MockResponse = MockResponse(code = 200, body = serde.encodeToString(Response.Success(listOf(data))))
    }

    abstract class MessageResponse : Interaction<Message> {
        override fun toResponse(): MockResponse = MockResponse(
                body = serde.encodeToString(Response.Success(data)),
        )
    }

    abstract class BooleanResponse<T> : Interaction<T> {
        override fun toResponse(): MockResponse = MockResponse(
                body = serde.encodeToString(Response.Success(true)),
        )
    }

    data class User(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Update,
            @Volatile
            override var completed: Boolean = false,
    ) : UpdateResponse()

    data class Bot(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Message,
            @Volatile
            override var completed: Boolean = false,
    ) : MessageResponse()

    data class Poll(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Message,
            @Volatile
            override var completed: Boolean = false,
    ) : MessageResponse()

    data class PollAnswer(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Update,
            @Volatile
            override var completed: Boolean = false,
    ) : UpdateResponse()

    data class Pin(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Long,
            @Volatile
            override var completed: Boolean = false,
    ) : BooleanResponse<Long>()

    data class Unpin(
            override val index: Int,
            override val time: Instant? = null,
            override val data: Long,
            @Volatile
            override var completed: Boolean = false,
    ) : BooleanResponse<Long>()

    data class Delete(
            override val index: Int,
            override val time: Instant? = null,
            override val data: List<Long>,
            @Volatile
            override var completed: Boolean = false,
    ) : BooleanResponse<List<Long>>()

    data class Empty(
            override val index: Int = -1,
            override val time: Instant? = null,
            override val data: Unit = Unit,
            @Volatile
            override var completed: Boolean = false,
    ) : Interaction<Unit> {
        override fun toResponse(): MockResponse =
                MockResponse(code = 200, body = serde.encodeToString(Response.Success(true)))

    }

    @BuilderDsl
    class Builder(init: Builder.() -> Unit) {
        private var index = -1
        private var time: Instant? = null
        private var interaction: Interaction<*>? = null

        init {
            init()
        }

        fun index(index: Int) {
            this.index = index
        }

        fun poll(message: Message) {
            interaction = Poll(index, time, message)
        }

        fun pollAnswer(update: Update) {
            interaction = PollAnswer(index, time, update)
        }

        fun user(update: Update) {
            interaction = User(index, time, update)
        }

        fun bot(message: Message) {
            interaction = Bot(index, time, message)
        }

        fun time(time: Instant) {
            this.time = time
        }

        fun pin(messageId: Long) {
            interaction = Pin(index, time, messageId)
        }

        fun unpin(messageId: Long) {
            interaction = Unpin(index, time, messageId)
        }

        fun delete(messageIds: List<Long>) {
            interaction = Delete(index, time, messageIds)
        }

        fun build(): Interaction<*> {
            check(index > -1) { "Scenario index should be specified and positive" }
            check(interaction != null) { "Interaction should be specified" }
            return interaction!!
        }
    }
}
