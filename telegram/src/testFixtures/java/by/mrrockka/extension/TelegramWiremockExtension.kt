package by.mrrockka.extension

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.wait.strategy.Wait

class TelegramWiremockExtension : BeforeAllCallback, AfterEachCallback {
    override fun beforeAll(context: ExtensionContext?) {
        TelegramWiremockContainer.start()
        System.setProperty("wiremock.server.baseUrl", TelegramWiremockContainer.baseUrl)
        TelegramWiremockContainer.waitingFor(Wait.forHealthcheck())
    }

    override fun afterEach(context: ExtensionContext?) {
    }
}
