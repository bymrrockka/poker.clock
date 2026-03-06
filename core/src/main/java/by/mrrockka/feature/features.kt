package by.mrrockka.feature

import by.mrrockka.domain.scaleDown
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
@ConfigurationProperties(prefix = "feature.service-fee")
class ServiceFeeFeature(
        var enabled: Boolean = false,
        var percent: BigDecimal = BigDecimal.ZERO,
        var threshold: BigDecimal = BigDecimal("100"),
) {

    fun calculate(amount: BigDecimal): BigDecimal = if (enabled) {
        val result = (amount * percent / BigDecimal("100")).scaleDown()
        if ((amount / result) < threshold) BigDecimal.ZERO else result
    } else BigDecimal.ZERO

}
