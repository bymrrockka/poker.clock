package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.interfaces.ctx.ClassManager
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Configuration
open class BotConfig(
        private val botProps: BotProperties,
) {

    @Bean
    open fun bot(appContext: ApplicationContext): TelegramBot {
        return TelegramBot(botProps.token) {
            classManager = SpringClassManager(appContext)
            commandParsing {
                commandDelimiter = '\n'
                restrictSpacesInCommands = true
            }
            exceptionHandlingStrategy = ExceptionHandlingStrategy.Handle(PokerClockExceptionHandler)
        }
    }

    @Bean
    @OptIn(ExperimentalTime::class)
    open fun clock(): Clock {
        return Clock.System
    }
}

@Configuration
open class SpringClassManager(
        private val applicationContext: ApplicationContext,
) : ClassManager {
    override fun getInstance(kClass: KClass<*>, vararg initParams: Any?): Any =
            applicationContext.getBean(kClass.java, *initParams)
}

@Component
@ConfigurationProperties(prefix = "bot.description")
class BotCommandDescriptions {
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
class BotProperties {
    lateinit var name: String
    lateinit var nickname: String
    lateinit var token: String
    var enabled: Boolean = false
    val botpath: String by lazy { "/bot$token" }
}

data class CommandDescription(val enabled: Boolean, val description: String?, val details: String?, val alias: String?)