package by.mrrockka.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

data class CommandDescription(val enabled: Boolean, val description: String?, val details: String?, val alias: String?)

@Component
@ConfigurationProperties(prefix = "bot.description")
class BotDescriptionProperties {
    lateinit var commands: Map<String, CommandDescription>
    val byNamesAndAliases: Map<String, CommandDescription> by lazy {
        commands.entries
                .flatMap { (key, value) ->
                    if (value.alias != null)
                        setOf(value.alias to value, key to value)
                    else setOf(key to value)
                }
                .toMap()
    }
}

@Component
@ConfigurationProperties(prefix = "bot.properties")
class TelegramBotsProperties {
    lateinit var name: String
    lateinit var nickname: String
    lateinit var token: String
    var enabled: Boolean = false
    val botpath: String by lazy { "/bot$token" }
}
