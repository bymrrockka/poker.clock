package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.MessageEntityCreator
import by.mrrockka.creator.SendMessageCreator
import by.mrrockka.creator.UpdateCreator
import com.marcinziolo.kotlin.wiremock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus


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

        val expected = SendMessageCreator.api {
            it.text("Cash game started.")
        }

        wireMock.post {
            priority = 1
            url equalTo "/bottoken/getupdates"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${updates.toJsonString()}
            }
            """
        }

        wireMock.post {
            priority = 2
            url equalTo "/bottoken/getupdates"
        } returnsJson {
            body = """
            {
                "ok": "true",
                "result": ${UpdateCreator.emptyList().toJsonString()}
            }
            """
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

        wireMock.verify {

        }
        /*

                When {
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
                }

                Then {
                    messageReceived {
                        message = """
                            Game created successfully!
                        """.trimIndent()
                    }
                }*/
    }
}
