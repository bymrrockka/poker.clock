package by.mrrockka.domain

import by.mrrockka.feature.ServiceFeeFeature
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

interface GameSummary {
    val person: Person
    val buyIn: BigDecimal

    fun total(): BigDecimal
    fun entries(): BigDecimal
}

interface PrizeGameSummary : GameSummary {
    val entriesNum: Int
    val position: Int?
    val prize: BigDecimal
}

data class TournamentSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val position: Int? = null,
        override val prize: BigDecimal,
) : PrizeGameSummary {
    override fun total(): BigDecimal = prize - entries()
    override fun entries(): BigDecimal = buyIn * BigDecimal(entriesNum)
}

data class BountyTournamentSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        override val entriesNum: Int,
        override val prize: BigDecimal,
        override val position: Int? = null,
        val bounty: BountySummary,
) : PrizeGameSummary {
    override fun total(): BigDecimal = bounty.total + prize - entries()
    override fun entries(): BigDecimal = buyIn * BigDecimal(entriesNum)
}

data class BountySummary(
        val amount: BigDecimal,
        val takenNum: Int,
        val givenNum: Int,
) {
    val given: BigDecimal by lazy { BigDecimal(givenNum) * amount }
    val taken: BigDecimal by lazy { BigDecimal(takenNum) * amount }
    val total: BigDecimal by lazy { taken - given }
}

data class CashSummary(
        override val person: Person,
        override val buyIn: BigDecimal,
        val withdrawals: BigDecimal,
) : GameSummary {
    override fun total(): BigDecimal = withdrawals - entries()
    override fun entries(): BigDecimal = buyIn
}

data class FinalPlace(val position: Int, val person: Person)

@Serializable
data class PositionPrize(val position: Int, @Serializable(with = BigDecimalSerializer::class) val percentage: BigDecimal)
internal data class FinalPrizeSummary(val position: Int, val amount: BigDecimal, val person: Person)

fun TournamentGame.gameSummary(serviceFee: ServiceFeeFeature): List<TournamentSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizeSummary(prizePool!!, finalePlaces!!, serviceFee)

    return players.map { player ->
        val prize = prizeSummary[player.person]
        TournamentSummary(
                person = player.person,
                buyIn = buyIn,
                entriesNum = player.entries.size,
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
        )
    }
}

fun BountyTournamentGame.gameSummary(featureServiceFee: ServiceFeeFeature): List<BountyTournamentSummary> {
    checkNotNull(finalePlaces) { "Can't calculate with no finale places" }
    checkNotNull(prizePool) { "Can't calculate with no prize pool" }

    val prizeSummary = prizeSummary(prizePool!!, finalePlaces!!, featureServiceFee)

    return players.map { player ->
        val prize = prizeSummary[player.person]
        val (taken, given) = player.takenToGiven()
        BountyTournamentSummary(
                person = player.person,
                buyIn = buyIn,
                entriesNum = player.entries.size,
                prize = prize?.amount ?: BigDecimal.ZERO,
                position = prize?.position,
                bounty = BountySummary(
                        amount = bounty,
                        takenNum = taken.size,
                        givenNum = given.size,
                ),
        )
    }
}

fun CashGame.gameSummary(featureServiceFee: ServiceFeeFeature): List<CashSummary> = players.map { player ->
    CashSummary(
            person = player.person,
            buyIn = player.entries.total(),
            withdrawals = player.withdrawals.total(),
    )
}

private fun Game.prizeSummary(prizePoll: List<PositionPrize>, finalePlaces: List<FinalPlace>, serviceFee: ServiceFeeFeature): Map<Person, FinalPrizeSummary> {
    val serviceFeeAmount = serviceFee.calculate(total())
    val total = total() - serviceFeeAmount
    var left = total
    return prizePoll.sortedBy { it.position }
            .zip(finalePlaces.sortedBy { it.position })
            .mapIndexed { index, (prize, place) ->
                if (finalePlaces.size - 1 <= index) FinalPrizeSummary(
                        position = place.position,
                        person = place.person,
                        amount = left,
                )
                else {
                    val amount = (total * prize.percentage / BigDecimal("100")).scaleDown()
                    left -= amount
                    FinalPrizeSummary(
                            position = place.position,
                            person = place.person,
                            amount = amount,
                    )
                }
            }.associateBy { it.person }
}

@OptIn(ExperimentalSerializationApi::class)
private object BigDecimalSerializer : KSerializer<BigDecimal> {
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

