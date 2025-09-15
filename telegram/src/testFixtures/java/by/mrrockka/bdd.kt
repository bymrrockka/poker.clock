package by.mrrockka

import org.apache.commons.lang3.RandomStringUtils

interface Command {
    data class Message(val replyTo: String? = null, var message: String) : Command {
        val botcommand: String by lazy { "^(/([\\w]+))".toRegex(RegexOption.MULTILINE).find(message)!!.destructured.component1() }
    }

    class Poll : Command
}

class GivenSpecification {
    val scenarioSeed: String = RandomStringUtils.randomAlphabetic(5)
    var commands: List<Command> = mutableListOf()

    fun message(replyTo: String? = null, init: () -> String) {
        this.commands += Command.Message(replyTo, init())
    }

    fun pollPosted() {
        this.commands += Command.Poll()
    }
}

class WhenSpecification(val scenarioSeed: String, val commands: List<Command>)

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.scenarioSeed, this.commands)
        .apply { block() }
