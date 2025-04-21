package by.mrrockka.extension

import org.wiremock.integrations.testcontainers.WireMockContainer

object TelegramWiremockContainer : WireMockContainer("$OFFICIAL_IMAGE_NAME:3.10.0") {
    init {
        withMappingFromResource("telegram", "wiremock/mappings.json")
    }
}
