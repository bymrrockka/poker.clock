package by.mrrockka.scenario

import by.mrrockka.creator.MessageEntityCreator
import by.mrrockka.creator.SendMessageCreator
import org.apache.commons.lang3.RandomStringUtils
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import kotlin.text.RegexOption.MULTILINE


class Command(init: Command.() -> Unit) {
    lateinit var message: String
    var entities: List<MessageEntity> = mutableListOf()

    init {
        init(this)
    }

    fun message(message: String) {
        this.message = message.trim()
        val botCommandRegex = "^(/[\\w_]+)".toRegex(MULTILINE)
        val mentionRegex = "(@[\\w\\d_]+)".toRegex(MULTILINE)
        val botCommand = botCommandRegex.find(this.message)!!.groups[0]!!.value
        entity(botCommand, EntityType.BOTCOMMAND)

        mentionRegex.findAll(this.message)
                .map { it.groups[0]!!.value }
                .distinct()
                .forEach { mention -> entity(mention, EntityType.MENTION) }
    }

    fun entity(value: String, type: String) {
        when (type) {
            EntityType.BOTCOMMAND -> entities += MessageEntityCreator.apiCommand(message, value)
            EntityType.MENTION -> entities += MessageEntityCreator.apiMention(message, value)
        }
    }
}

class GivenSpecification {
    val scenarioSeed: String = RandomStringUtils.randomAlphabetic(5)
    var commands: List<Command> = mutableListOf()

    fun command(init: Command.() -> Unit) {
        this.commands += Command(init)
    }
}

class WhenSpecification(val scenarioSeed: String, val commands: List<Command>? = null) {}

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

fun Given(block: GivenSpecification.() -> Unit): GivenSpecification = GivenSpecification().apply(block)

//todo: refactor
infix fun GivenSpecification.When(block: GivenSpecification.() -> Unit): WhenSpecification = WhenSpecification(this.scenarioSeed, this.commands)
        .apply { block() }

infix fun WhenSpecification.Then(block: ThenSpecification.() -> Unit) = ThenSpecification(this.scenarioSeed)
        .apply(block)