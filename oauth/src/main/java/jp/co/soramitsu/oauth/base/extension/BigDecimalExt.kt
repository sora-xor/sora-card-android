package jp.co.soramitsu.oauth.base.extension

import java.math.BigDecimal
import java.math.RoundingMode
import jp.co.soramitsu.oauth.base.extension.BigDecimalExt.DEFAULT_SCALE
import kotlin.math.max

object BigDecimalExt {
    const val DEFAULT_SCALE = 18
}

fun BigDecimal.greaterThan(a: BigDecimal) = this.compareTo(a) == 1

fun BigDecimal.divideBy(divisor: BigDecimal, scale: Int? = null): BigDecimal {
    return if (scale == null) {
        val maxScale = max(this.scale(), divisor.scale()).coerceAtMost(DEFAULT_SCALE)

        if (maxScale != 0) {
            this.divide(divisor, maxScale, RoundingMode.HALF_EVEN)
        } else {
            this.divide(divisor, DEFAULT_SCALE, RoundingMode.HALF_EVEN)
        }
    } else {
        this.divide(divisor, scale, RoundingMode.HALF_EVEN)
    }
}
