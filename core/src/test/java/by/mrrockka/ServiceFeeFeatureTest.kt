package by.mrrockka

import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Game
import by.mrrockka.domain.Player
import by.mrrockka.domain.PositionPrize
import by.mrrockka.extension.textApprover
import by.mrrockka.feature.ServiceFeeFeature
import by.mrrockka.service.AmountState
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

abstract class ServiceFeeFeatureTest : AbstractCalculatorTest() {

    abstract fun game(buyin: BigDecimal = BigDecimal("10"), playersSize: Int, prizeSize: Int = 1): Game

    @ParameterizedTest
    @ValueSource(ints = [3, 5, 7, 8, 11, 14, 16, 30])
    fun `calculate with service fee enabled`(size: Int) {
        val feature = ServiceFeeFeature(
                enabled = true,
                percent = BigDecimal("13"),
                threshold = BigDecimal("1"),
                description = "Service Fee",
                url = "https://www.mrrockka.by",
        )
        changeFeeTo(feature)

        val game = game(playersSize = size, prizeSize = 2)

        game.calculateAndAssert(textApprover("calculate with service fee enabled. size $size"))
    }

    @Test
    fun `when calculated fee goes beyond threshold then fee shouldn't affect game calculations`(approver: Approver) {
        val feature = ServiceFeeFeature(
                enabled = true,
                percent = BigDecimal("13"),
                threshold = BigDecimal("100"),
                description = "Service Fee",
                url = "https://www.mrrockka.by",
        )
        changeFeeTo(feature)

        val game = game(playersSize = 3, prizeSize = 1)

        game.calculateAndAssert(approver)
    }

    protected fun prizePool(size: Int): List<PositionPrize> {
        val hundred = BigDecimal("100.0")
        val state = AmountState(hundred)
        return (1..size).map { PositionPrize(it, state.decreaseAndGet(hundred / size.toBigDecimal())) }
    }

    protected fun List<Player>.finalePlaces(size: Int): List<FinalPlace> = take(size)
            .mapIndexed { index, player -> FinalPlace(index + 1, player.person) }
}