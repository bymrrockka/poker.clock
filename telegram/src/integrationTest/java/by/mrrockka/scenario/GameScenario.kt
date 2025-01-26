package by.mrrockka.scenario

import by.mrrockka.bot.TelegramBotsProperties
import by.mrrockka.config.TelegramPSQLExtension
import by.mrrockka.config.TestBotConfig
import com.github.tomakehurst.wiremock.WireMockServer
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.verify
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import java.util.concurrent.TimeUnit


@ExtendWith(TelegramPSQLExtension::class)
@EnableWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [TestBotConfig::class])
@ActiveProfiles("repository")
class GameScenario {

    @InjectWireMock
    lateinit var wireMock: WireMockServer

    @Autowired
    lateinit var botProps: TelegramBotsProperties

    @Test
    fun `user sent command to create a game and receive successful message`() {
        await.atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMock.verify {
                url equalTo "/${botProps.token}setMyCommands"
                atLeast = 1
            }
        }
    }

}