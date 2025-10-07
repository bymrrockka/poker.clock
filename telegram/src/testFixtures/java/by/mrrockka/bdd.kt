package by.mrrockka

import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.domain.Person
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface Command {
    val unique: String


    data class Message(var message: String, val replyTo: Command? = null, override val unique: String = unique()) : Command

    @OptIn(ExperimentalTime::class)
    data class Poll(val time: Instant, override val unique: String = unique()) : Command

    data class PollAnswer(val poll: Poll, val person: Person, val optionName: String? = null, val option: Int, override val unique: String = unique()) : Command

    data class PinMessage(val command: Command, override val unique: String = unique()) : Command

    companion object {
        fun unique(): String = telegramRandoms.faker.regexify("\\w{10,12}")
    }
}

class GivenSpecification {
    var commands: List<Command> = mutableListOf()

    fun message(replyTo: Command? = null, init: () -> String): Command.Message {
        val command = Command.Message(replyTo = replyTo, message = init())
        this.commands += command
        return command
    }

    @OptIn(ExperimentalTime::class)
    fun pollPosted(time: Instant): Command.Poll {
        val command = Command.Poll(time)
        this.commands += command
        return command
    }

    fun Command.Poll.pollAnswer(person: Person, option: Int) {
        this@GivenSpecification.commands += Command.PollAnswer(this, person, option = option)
    }

    fun Command.pinned() {
        this@GivenSpecification.commands += Command.PinMessage(this)
    }
}

class WhenSpecification(val commands: List<Command>)

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.commands)
        .apply { block() }

infix fun WhenSpecification.Then(block: WhenSpecification.() -> Unit) = block()
