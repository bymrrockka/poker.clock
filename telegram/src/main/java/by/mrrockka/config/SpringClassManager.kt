package by.mrrockka.config

import eu.vendeli.tgbot.interfaces.ctx.ClassManager
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import kotlin.reflect.KClass

@Configuration
open class SpringClassManager(
        private val applicationContext: ApplicationContext,
) : ClassManager {
    override fun getInstance(kClass: KClass<*>, vararg initParams: Any?): Any =
            applicationContext.getBean(kClass.java, *initParams)
}
