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
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(classes = [TestConfig::class])
abstract class StubTest {
    private var chatid: Long = -1L
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
        when (this) {
            is Command.Member -> {
                dispatcher.member(member)
            }

            is Command.PollAnswer -> {
                val update = update {
                    pollAnswer {
                        pollId(messageLog[this@stub.poll]!!.poll!!.id)
                        option(option - 1)
                        user(person.toUser())
                    }
                }

                dispatcher.scenario {
                    index(index)
                    update(update)
                }
            }

            is Command.UserMessage -> {
                val message = message {
                    id(index.toLong() + 1)
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
                    update(update)
                }
            }

            is Command.BotMessage -> {
                val message = message {
                    id(index.toLong() + 1)
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
                    message(message)
                }
            }

            is Command.Poll -> {
                val message = message {
                    id(index.toLong() + 1)
                    chatId(chatid)
                    poll()
                }
                messageLog += this to message

                dispatcher.scenario {
                    index(index)
                    poll(message)
                    time(time)
                }
            }

            is Command.Pin -> {
                dispatcher.scenario {
                    index(index)
                    pin()
                }
            }

            is Command.Unpin -> {
                dispatcher.scenario {
                    index(index)
                    unpin()
                }
            }

            is Command.DeleteMessages -> {
                dispatcher.scenario {
                    index(index)
                    delete()
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