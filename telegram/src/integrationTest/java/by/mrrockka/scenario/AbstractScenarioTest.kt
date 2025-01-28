package by.mrrockka.scenario

import by.mrrockka.config.TelegramPSQLExtension
import by.mrrockka.config.TestBotConfig
import com.github.tomakehurst.wiremock.client.WireMock
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.verify
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import org.wiremock.integrations.testcontainers.WireMockContainer.OFFICIAL_IMAGE_NAME
import java.time.Duration


@ExtendWith(TelegramPSQLExtension::class)
@ActiveProfiles("repository")
@Testcontainers
@SpringBootTest(classes = [TestBotConfig::class])
abstract class AbstractScenarioTest {
    companion object {
        @Container
        val container = WireMockContainer("$OFFICIAL_IMAGE_NAME:3.10.0")
                .withMappingFromResource("telegram", "wiremock/mappings.json")

        lateinit var wireMock: WireMock

        @JvmStatic
        @BeforeAll
        fun beforeAll(): Unit {
            container.start()
            wireMock = WireMock(container.port)
            System.setProperty("wiremock.server.baseUrl", container.baseUrl)
        }

        @JvmStatic
        @AfterAll
        fun afterAll(): Unit {
            await.atMost(Duration.ofSeconds(3)).untilAsserted {
                wireMock.verify {
                    url equalTo "/blah"
                    atLeast = 1
                }

                wireMock.verify {
                    url equalTo "/param"
                    atLeast = 1
                }
            }
        }
    }


}