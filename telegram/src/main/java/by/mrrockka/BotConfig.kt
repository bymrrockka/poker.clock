package by.mrrockka

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.interfaces.ctx.ClassManager
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Configuration
open class BotConfig(
        private val botProps: BotProperties,
) {

    @Bean
    @Profile("production")
    @OptIn(DelicateCoroutinesApi::class)
    open fun bot(appContext: ApplicationContext): TelegramBot {
        val bot = TelegramBot(botProps.token) {
            classManager = SpringClassManager(appContext)
            commandParsing {
                commandDelimiter = '\n'
                restrictSpacesInCommands = true
            }
            exceptionHandlingStrategy = ExceptionHandlingStrategy.Handle(PokerClockExceptionHandler)
        }

        GlobalScope.launch {
            bot.handleUpdates()
        }

        return bot
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
class BotCommands {
    lateinit var commands: List<Description>
    val byNameAndAlias: Map<String, Description> by lazy {
        commands.flatMap { setOf(it.name to it) + if (it.alias != null) setOf(it.alias to it) else emptySet() }.toMap()
    }

    data class Description(val name: String, val enabled: Boolean, val description: String?, val details: String?, val alias: String?)
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

