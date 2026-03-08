package by.mrrockka.domain

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal

data class FinalPlace(val position: Int, val person: BasicPerson)

@Serializable
data class PositionPrize(val position: Int, @Serializable(with = BigDecimalSerializer::class) val percentage: BigDecimal)

@OptIn(ExperimentalSerializationApi::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override fun serialize(encoder: Encoder, value: BigDecimal) =
            when (encoder) {
                is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral(value.toPlainString()))
                else -> encoder.encodeString(value.toPlainString())
            }

    override val descriptor = PrimitiveSerialDescriptor("java.math.BigDecimal", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal =
            when (decoder) {
                is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content.toBigDecimal()
                else -> decoder.decodeString().toBigDecimal()
            }
}
