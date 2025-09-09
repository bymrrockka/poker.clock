package by.mrrockka

import by.mrrockka.TelegramRandoms.Companion.telegramRandoms
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.AfterEach

abstract class AbstractTest {

    @AfterEach
    fun afterEach() {
        telegramRandoms.resetRandom()
    }

    val objectMapper = ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun Any?.toJsonString() = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}