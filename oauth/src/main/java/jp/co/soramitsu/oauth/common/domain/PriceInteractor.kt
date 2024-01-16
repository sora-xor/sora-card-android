package jp.co.soramitsu.oauth.common.domain

import java.math.BigDecimal
import jp.co.soramitsu.oauth.base.extension.divideBy
import jp.co.soramitsu.oauth.base.extension.greaterThan
import jp.co.soramitsu.oauth.common.model.EuroLiquiditySufficiency
import jp.co.soramitsu.oauth.common.model.XorLiquiditySufficiency

class TokenSufficient(
    val realRequiredBalance: BigDecimal,
    val needToken: BigDecimal,
    val needEuro: BigDecimal,
)

interface PriceInteractor {

    companion object {
        const val KYC_REQUIRED_BALANCE: Double = 100.0
        private val KYC_REAL_REQUIRED_BALANCE: BigDecimal = BigDecimal.valueOf(95)
        private val KYC_REQUIRED_BALANCE_WITH_BACKLASH: BigDecimal = BigDecimal.valueOf(
            KYC_REQUIRED_BALANCE,
        )
        fun calcSufficient(balance: BigDecimal, ratio: Double): TokenSufficient {
            val bigRatio = BigDecimal.valueOf(ratio)
            val xorRequiredBalanceWithBacklash =
                KYC_REQUIRED_BALANCE_WITH_BACKLASH.divideBy(bigRatio)
            val xorRealRequiredBalance =
                KYC_REAL_REQUIRED_BALANCE.divideBy(bigRatio)
            val xorBalanceInEur = balance.multiply(bigRatio)
            val needInXor =
                if (balance.greaterThan(xorRealRequiredBalance)) {
                    BigDecimal.ZERO
                } else {
                    xorRequiredBalanceWithBacklash.minus(balance)
                }
            val needInEur =
                if (xorBalanceInEur.greaterThan(KYC_REAL_REQUIRED_BALANCE)) {
                    BigDecimal.ZERO
                } else {
                    KYC_REQUIRED_BALANCE_WITH_BACKLASH.minus(xorBalanceInEur)
                }
            return TokenSufficient(
                realRequiredBalance = xorRealRequiredBalance,
                needToken = needInXor,
                needEuro = needInEur,
            )
        }
    }

    suspend fun calculateXorLiquiditySufficiency(): Result<XorLiquiditySufficiency>

    suspend fun calculateEuroLiquiditySufficiency(): Result<EuroLiquiditySufficiency>

    suspend fun calculateCardIssuancePrice(): String

    suspend fun calculateKycAttemptPrice(): String
}
