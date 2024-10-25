package by.mrrockka.config

import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
open class TelegramConfiguration {

    @Bean
    open fun database(dataSource: DataSource): Database {
        return Database.connect(dataSource)
    }
}