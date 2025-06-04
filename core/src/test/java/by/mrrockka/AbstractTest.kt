package by.mrrockka

import by.mrrockka.extension.JsonApproverExtension
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JsonApproverExtension::class)
abstract class AbstractTest {
    val objectMapper = ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun Any?.toJsonString() = objectMapper.writeValueAsString(this)

}