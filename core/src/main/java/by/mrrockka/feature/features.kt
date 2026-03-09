package by.mrrockka.feature

import by.mrrockka.domain.ServiceFee
import by.mrrockka.service.scaleDown
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
@ConfigurationProperties(prefix = "feature.service-fee")
class ServiceFeeFeature(
        var enabled: Boolean = false,
        var percent: BigDecimal = BigDecimal.ZERO,
        var threshold: BigDecimal = BigDecimal("100"),
        var description: String = "",
        val url: String = "",
) {
    val feePerson: ServiceFee by lazy { ServiceFee(description, url) }

    fun calculate(amount: BigDecimal): BigDecimal = if (enabled) {
        val result = (amount * percent / BigDecimal("100.0")).scaleDown()
        if ((amount / result) < threshold) BigDecimal.ZERO else result
    } else BigDecimal.ZERO

}
