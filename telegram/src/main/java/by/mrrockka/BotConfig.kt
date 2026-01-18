package by.mrrockka

import by.mrrockka.commands.GameWizardHandler
import by.mrrockka.service.GameTelegramService
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.botactions.setMyCommands
import eu.vendeli.tgbot.interfaces.ctx.ClassManager
import eu.vendeli.tgbot.types.bot.BotCommand
import eu.vendeli.tgbot.types.component.ExceptionHandlingStrategy
import eu.vendeli.tgbot.types.component.UpdateType
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.BeansException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
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
    @DependsOn("liquibase")
    @OptIn(DelicateCoroutinesApi::class)
    open fun bot(appContext: ApplicationContext, botCommands: BotCommands): TelegramBot {
        val bot = TelegramBot(botProps.token) {
            classManager = SpringClassManager(appContext, classManager)
            identifier = botProps.name
            commandParsing {
                commandDelimiter = '\n'
                restrictSpacesInCommands = true
            }
            exceptionHandlingStrategy = ExceptionHandlingStrategy.Handle(PokerClockExceptionHandler)
        }

        GlobalScope.launch {
            setMyCommands(
                    command = botCommands.commands
                            .filter { it.enabled }
                            .map { BotCommand(command = it.name, description = it.description ?: "No description") },
            ).send(bot)
            bot.handleUpdates(
                    listOf(
                            UpdateType.MESSAGE,
                            UpdateType.POLL_ANSWER,
                    ),
            )
        }

        return bot
    }

    @Bean
    @OptIn(ExperimentalTime::class)
    open fun clock(): Clock {
        return Clock.System
    }

    @Bean
    open fun gameWizard(gameService: GameTelegramService): GameWizardHandler {
        GameWizardHandler.gameService = gameService
        return GameWizardHandler
    }
}

open class SpringClassManager(
        private val applicationContext: ApplicationContext,
        private val classManager: ClassManager,
) : ClassManager {
    override fun getInstance(kClass: KClass<*>, vararg initParams: Any?): Any {
        var instance: Any

        try {
            instance = applicationContext.getBean(kClass.java, *initParams)
        } catch (beanEx: BeansException) {
            instance = classManager.getInstance(kClass, *initParams)
        }
        return instance
    }
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

