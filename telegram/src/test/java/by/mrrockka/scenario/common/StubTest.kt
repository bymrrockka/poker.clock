package by.mrrockka.scenario.common

import by.mrrockka.Command
import by.mrrockka.CoreRandoms.Companion.coreRandoms
import by.mrrockka.GivenSpecification
import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import by.mrrockka.builder.message
import by.mrrockka.builder.toUser
import by.mrrockka.builder.update
import by.mrrockka.builder.user
import by.mrrockka.scenario.TestClock
import by.mrrockka.scenario.TestConfig
import by.mrrockka.scenario.interaction.MockDispatcher
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.msg.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

private val logger = KotlinLogging.logger {}

@Testcontainers
@SpringBootTest(classes = [TestConfig::class])
abstract class StubTest {
    protected var chatid: Long = -1L
    protected lateinit var mainUser: User
    protected val messageLog = mutableMapOf<Command, Message>()
    protected val users = mutableMapOf<String, User>()

    @Autowired
    lateinit var dispatcher: MockDispatcher

    @Autowired
    lateinit var clock: TestClock

    @BeforeEach
    fun setup() {
        coreRandoms.reset()
        telegramRandoms.reset()
        dispatcher.reset()
        users.clear()
        messageLog.clear()
        chatid = telegramRandoms.chatid()
        mainUser = user()
        users[mainUser.username!!] = mainUser
    }

    fun GivenSpecification.updatesReceived() {
        check(commands.isNotEmpty()) { "Commands should be specified" }
        commands.forEachIndexed { index, command -> command.stub(index) }
    }

    private fun Command.stub(index: Int) {
        when (val command = this) {
            is Command.Member -> {
                dispatcher.member(member)
            }

            is Command.PollAnswer -> {
                val update = update {
                    pollAnswer {
                        pollId(messageLog[command.poll]!!.poll!!.id)
                        option(option - 1)
                        user(person.toUser())
                    }
                }

                dispatcher.scenario {
                    index(index)
                    pollAnswer(update)
                }
            }

            is Command.UserMessage -> {
                val message = message {
                    id(index.toLong())
                    text(message)
                    chatId(chatid)
                    from(username?.get() ?: mainUser)
                    createdAt(clock.now())
                    if (replyTo != null && messageLog[replyTo] != null) {
                        replyTo(messageLog[replyTo]!!)
                    }
                }

                messageLog += this to message

                val update = update { message(message) }
                dispatcher.scenario {
                    index(index)
                    user(update)
                }
            }

            is Command.BotMessage -> {
                val message = message {
                    id(index.toLong())
                    text(message)
                    chatId(chatid)
                    createdAt(clock.now())
                    if (replyTo != null && messageLog[replyTo] != null) {
                        replyTo(messageLog[replyTo]!!)
                    }
                }

                messageLog += this to message

                dispatcher.scenario {
                    index(index)
                    bot(message)
                }
            }

            is Command.Poll -> {
                val message = message {
                    id(index.toLong())
                    chatId(chatid)
                    poll()
                }
                messageLog += this to message

                dispatcher.scenario {
                    index(index)
                    time(time)
                    poll(message)
                }
            }

            is Command.Pin -> {
                dispatcher.scenario {
                    index(index)
                    pin(messageLog[command.command]?.messageId ?: error("Command was not found in log"))
                }
            }

            is Command.Unpin -> {
                dispatcher.scenario {
                    index(index)
                    unpin(messageLog[command.command]?.messageId ?: error("Command was not found in log"))
                }
            }

            is Command.DeleteMessages -> {
                dispatcher.scenario {
                    index(index)
                    command.toDelete
                            .mapNotNull {
                                val message = messageLog[it]
                                if (message == null) logger.warn { "Command $it was not found in log" }
                                message
                            }.map { it.messageId }
                            .also { delete(it) }
                }
            }

            else -> error("Command type haven't recognised")
        }
    }

    private fun String.get(): User {
        return users[this] ?: run {
            val user = user {
                username(this@get)
            }
            users[this] = user
            user
        }
    }
}