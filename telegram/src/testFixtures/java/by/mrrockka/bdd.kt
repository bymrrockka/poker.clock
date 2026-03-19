package by.mrrockka

import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.BasicPerson
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface Command {
    val unique: String

    data class UserMessage(var message: String, val replyTo: Command? = null, override val unique: String = unique()) : Command

    //no assertions for bot message
    data class BotMessage(var message: String, val replyTo: Command? = null, override val unique: String = unique()) : Command

    @OptIn(ExperimentalTime::class)
    data class Poll(val time: Instant, override val unique: String = unique()) : Command

    data class PollAnswer(val poll: Poll, val person: BasicPerson, val optionName: String? = null, val option: Int, override val unique: String = unique()) : Command

    data class Pin(val command: Command, override val unique: String = unique()) : Command

    data class Unpin(val command: Command, override val unique: String = unique()) : Command

    data class DeleteMessages(val toDelete: List<Command>, override val unique: String = unique()) : Command

    companion object {
        fun unique(): String = telegramRandoms.faker.regexify("\\w{10,12}")
    }
}

class GivenSpecification {
    var commands: List<Command> = mutableListOf()

    fun user(replyTo: Command? = null, init: () -> String): Command.UserMessage {
        val command = Command.UserMessage(replyTo = replyTo, message = init())
        this.commands += command
        return command
    }

    fun bot(replyTo: Command? = null, init: () -> String): Command.BotMessage {
        val command = Command.BotMessage(replyTo = replyTo, message = init())
        this.commands += command
        return command
    }

    @OptIn(ExperimentalTime::class)
    fun pollPosted(time: Instant): Command.Poll {
        val command = Command.Poll(time)
        this.commands += command
        return command
    }

    fun Command.Poll.pollAnswer(person: BasicPerson, option: Int) {
        this@GivenSpecification.commands += Command.PollAnswer(this, person, option = option)
    }

    fun Command.pinned() {
        this@GivenSpecification.commands += Command.Pin(this)
    }

    fun Command.unpinned() {
        this@GivenSpecification.commands += Command.Unpin(this)
    }

    fun unpinned(vararg commands: Command) {
        commands.forEach { command ->
            this@GivenSpecification.commands += Command.Unpin(command)
        }
    }

    fun List<Command>.deleted() {
        commands += Command.DeleteMessages(this)
    }
}

class WhenSpecification(val commands: List<Command>)

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.commands)
        .apply { block() }

/**
 * This function should be replaced with exact implementation of tests
 */
infix fun WhenSpecification.Then(block: WhenSpecification.() -> Unit) = block()
