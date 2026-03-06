package by.mrrockka.domain

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.scaleDown(): BigDecimal = setScale(0, RoundingMode.HALF_DOWN)