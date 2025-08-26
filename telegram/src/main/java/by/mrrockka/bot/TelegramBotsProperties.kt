package by.mrrockka.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "telegrambots")
class TelegramBotsProperties {
    lateinit var name: String
    lateinit var nickname: String
    lateinit var token: String
    var enabled: Boolean = false
    val botpath: String by lazy { "/bot$token" }
}
