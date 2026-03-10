package by.mrrockka.domain

import kotlinx.serialization.Serializable
import java.math.BigDecimal

data class FinalPlace(val position: Int, val person: BasicPerson)

@Serializable
data class PositionPrize(val position: Int, @Serializable(with = BigDecimalSerializer::class) val percentage: BigDecimal)