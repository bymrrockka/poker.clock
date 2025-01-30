package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.MessageEntityCreator
import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.creator.UpdateCreator
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.marcinziolo.kotlin.wiremock.*
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import java.time.Duration


class GameScenario : AbstractScenarioTest() {

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    val restTemplate = TestRestTemplate()

    @Test
    fun `check wiremock works`() {
        val blahResp = restTemplate.getForEntity<String>(container.getUrl("/blah"))
        assertThat(blahResp.body).isEqualTo("blah")
        assertThat(blahResp.statusCode).isEqualTo(HttpStatus.OK)

        wireMock.get {
            url equalTo "/param"
        } returns {
            statusCode = HttpStatus.OK.value()
            body = "param"
        }

        val paramResp = restTemplate.getForEntity<String>(container.getUrl("/param"))
        assertThat(paramResp.body).isEqualTo("param")
        assertThat(paramResp.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `user sent command to create a game and receive successful message`() {
//        given
        val command = """
                            /cash_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                            @nickname2
                            @nickname3
                        """.trimIndent()
        val entities = listOf(
                MessageEntityCreator.apiCommand(command, "/cash_game"),
                MessageEntityCreator.apiMention(command, "@nickname1"),
                MessageEntityCreator.apiMention(command, "@nickname2"),
                MessageEntityCreator.apiMention(command, "@nickname3")
        )


        val updates = listOf(
                UpdateCreator.update {
                    this.updateId = ++updateId
                    this.message = MessageCreator.message {
                        it.text = command
                        it.entities = entities
                    }
                }
        )

        Given {
            command {
                it.message = command
                it.entities = entities
            }
        } When {
            it.command.updateReceived()
        } Then {
            it.url = "/bottoken/sendmessage"
            it.result = SendMessageCreator.api {
                it.text("Cash game started.")
            }
        }

//then
        val expected = SendMessageCreator.api {
            it.text("Cash game started.")
        }

        wireMock.post {
            url equalTo "/bottoken/sendmessage"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${expected.toJsonString()}
            }
            """
        }


        await.atMost(Duration.ofSeconds(1)).untilAsserted {
            wireMock.verify {
                url equalTo "/bottoken/sendmessage"
                method = RequestMethod.POST
                body contains "text" equalTo expected.text
            }
        }


//when
        wireMock.post {
            url equalTo "/bottoken/getupdates"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${updates.toJsonString()}
            }
            """
        } and {
            toState = "no updates"
        }
//finally?
        wireMock.post {
            url equalTo "/bottoken/getupdates"
            whenState = "no updates"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${UpdateCreator.emptyList().toJsonString()}
            }
            """
        }

        /*

                Given {
                    command {
                        message = """
                            /cash_game
                            stack: 30k
                            buyin: 30
                            @nickname1
                            @nickname2
                            @nickname3
                        """.trimIndent()

                        mentions = listOf(
                                "@nickname1",
                                "@nickname2",
                                "@nickname3"
                        )
                    }
                } When {
                    updateReceived
                } Then {
                    messageReceived {
                        message = """
                            Game created successfully!
                        """.trimIndent()
                    }
                }*/
    }
}

class Command {
    lateinit var message: String
    lateinit var entities: List<MessageEntity>
}

class ScenarioSpecification {
    lateinit var command: Command

    fun command(init: (Command) -> Unit) {
        command = Command().apply(init)
    }
}

class Expected<T> {
    lateinit var url: String
    var result: T? = null
}

fun Command.updateReceived() {

}

fun Given(block: ScenarioSpecification.() -> Unit): ScenarioSpecification = block.run { ScenarioSpecification() }

infix fun ScenarioSpecification.When(block: (ScenarioSpecification) -> Unit): ScenarioSpecification = ScenarioSpecification().apply(block)

infix fun ScenarioSpecification.Then(block: (Expected<SendMessage>) -> Unit) = Expected<SendMessage>().also(block)
