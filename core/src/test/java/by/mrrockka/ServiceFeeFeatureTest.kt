package by.mrrockka

import by.mrrockka.domain.FinalPlace
import by.mrrockka.domain.Game
import by.mrrockka.domain.Player
import by.mrrockka.domain.PositionPrize
import by.mrrockka.feature.ServiceFeeFeature
import by.mrrockka.service.AmountState
import com.oneeyedmen.okeydoke.Approver
import org.junit.jupiter.api.Test
import java.math.BigDecimal

abstract class ServiceFeeFeatureTest : AbstractCalculatorTest() {

    abstract fun game(buyin: BigDecimal = BigDecimal("10"), playersSize: Int, prizeSize: Int = 1): Game

    @Test
    fun `calculate with service fee enabled`(approver: Approver) {
        val feature = ServiceFeeFeature(
                enabled = true,
                percent = BigDecimal("13"),
                threshold = BigDecimal("1"),
                description = "Service Fee",
                url = "https://www.mrrockka.by",
        )
        changeFeeTo(feature)

        val game = game(playersSize = 13, prizeSize = 3)

        game.calculateAndAssert(approver)
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