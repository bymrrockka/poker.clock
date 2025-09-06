package by.mrrockka

import by.mrrockka.builder.BddDsl
import by.mrrockka.creator.SendMessageCreator
import org.apache.commons.lang3.RandomStringUtils
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class Command(init: Command.() -> Unit) {
    lateinit var message: String

    init {
        init(this)
    }

    fun String.message() {
        this@Command.message = this.trim()
    }
}

class GivenSpecification {
    val scenarioSeed: String = RandomStringUtils.randomAlphabetic(5)
    var commands: List<Command> = mutableListOf()

    fun command(init: Command.() -> Unit) {
        this.commands += Command(init)
    }
}

class WhenSpecification(val scenarioSeed: String, val commands: List<Command>)

class ThenSpecification(val scenarioSeed: String) {
    var expects: List<Expect> = mutableListOf()

    fun expect(expect: Expect.() -> Unit) {
        this.expects += Expect().apply(expect)
    }
}

class Expect {
    lateinit var result: BotApiMethod<*>

    fun <T : BotApiMethod<*>> result(result: T) {
        this.result = result
    }

    inline fun <reified T : BotApiMethod<*>> text(text: String) {
        when {
            T::class.java.isAssignableFrom(SendMessage::class.java) -> {
                this.result = SendMessageCreator.api { it.text(text) }
            }

            else -> {
                error("Invalid type ${T::class.java}")
            }
        }
    }
}

@BddDsl
fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

//todo: refactor
@BddDsl
infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.scenarioSeed, this.commands)
        .apply { block() }

@BddDsl
infix fun WhenSpecification.Then(block: ThenSpecification.() -> Unit) = ThenSpecification(this.scenarioSeed)
        .apply(block)