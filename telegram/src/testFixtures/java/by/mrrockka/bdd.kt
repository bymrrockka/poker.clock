package by.mrrockka

import by.mrrockka.domain.Person
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface Command {
    data class Message(val replyTo: String? = null, var message: String) : Command {
        val botcommand: String by lazy { "^(/([\\w]+))".toRegex(RegexOption.MULTILINE).find(message)!!.destructured.component1() }
    }

    @OptIn(ExperimentalTime::class)
    data class Poll(val time: Instant) : Command

    data class PollAnswer(val person: Person, val optionName: String? = null, val option: Int) : Command

    data class PinMessage(val message: String) : Command
}

class GivenSpecification {
    var commands: List<Command> = mutableListOf()

    fun message(replyTo: String? = null, init: () -> String) {
        this.commands += Command.Message(replyTo, init())
    }

    @OptIn(ExperimentalTime::class)
    fun pollPosted(time: Instant) {
        this.commands += Command.Poll(time)
    }

    fun Person.pollAnswer(option: Int) {
        this@GivenSpecification.commands += Command.PollAnswer(this, option = option)
    }

    fun String.pinned() {
        this@GivenSpecification.commands += Command.PinMessage(this)
    }
}

class WhenSpecification(val commands: List<Command>)

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.commands)
        .apply { block() }

infix fun WhenSpecification.Then(block: WhenSpecification.() -> Unit) = block()
