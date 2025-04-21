package by.mrrockka.extension

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class TelegramWiremockExtension : BeforeAllCallback, AfterEachCallback {
    override fun beforeAll(context: ExtensionContext?) {
        TelegramWiremockContainer.start()
        System.setProperty("wiremock.server.baseUrl", TelegramWiremockContainer.baseUrl)
    }

    override fun afterEach(context: ExtensionContext?) {
    }
}
