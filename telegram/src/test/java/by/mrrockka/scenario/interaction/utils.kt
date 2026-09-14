package by.mrrockka.scenario.interaction

import mockwebserver3.RecordedRequest
import tools.jackson.databind.JsonNode

internal val mapper = tools.jackson.module.kotlin.jacksonObjectMapper()
internal fun RecordedRequest.toJson(): JsonNode = mapper.readTree(this.body?.toByteArray())
internal fun RecordedRequest.userId(): Long = toJson().findPath("user_id").asLong()
