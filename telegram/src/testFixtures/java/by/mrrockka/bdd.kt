package by.mrrockka

import org.apache.commons.lang3.RandomStringUtils

interface Command {
    data class Message(var message: String) : Command
    class Poll : Command
}

class GivenSpecification {
    val scenarioSeed: String = RandomStringUtils.randomAlphabetic(5)
    var commands: List<Command> = mutableListOf()

    fun message(init: () -> String) {
        this.commands += Command.Message(init.invoke())
    }

    fun pollPosted() {
        this.commands += Command.Poll()
    }
}

class WhenSpecification(val scenarioSeed: String, val commands: List<Command>)

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.scenarioSeed, this.commands)
        .apply { block() }
