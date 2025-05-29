package by.mrrockka

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.oneeyedmen.okeydoke.junit5.ApprovalsExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ApprovalsExtension::class)
abstract class AbstractTest {
    val objectMapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun Any?.toJsonString() = objectMapper.writeValueAsString(this)

}