package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.creator.MessageCreator
import by.mrrockka.creator.UpdateCreator
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.returns
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.telegram.telegrambots.meta.api.objects.ApiResponse


class GameScenario : AbstractScenarioTest() {

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    val restTemplate = TestRestTemplate()

    @BeforeEach
    fun init() {
//        println("###All stubs ${wireMock.listAllStubMappings().mappings}")
        /*
                wiremock.post {
                    url equalTo "/bottoken/getupdates"
                } returnsjson {
                    header = "Content-Type" to "application/json"
                    body = "{}"
                }*/
    }

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

    }

}