package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.oauth.base.compose.ScreenStatus

data class CardIssuanceScreenState(
    val screenStatus: ScreenStatus,
    val xorInsufficientAmount: Double,
    val euroInsufficientAmount: Double,
    val euroLiquidityThreshold: Double,
    val euroIssuanceAmount: String,
) {

    val isScreenLoading: Boolean = screenStatus === ScreenStatus.LOADING

    val freeCardIssuanceState: FreeCardIssuanceState by lazy {
        FreeCardIssuanceState(
            screenStatus = screenStatus,
            xorInsufficientAmount = xorInsufficientAmount,
            euroInsufficientAmount = euroInsufficientAmount,
            euroLiquidityThreshold = euroLiquidityThreshold
        )
    }

    val paidCardIssuanceState: PaidCardIssuanceState by lazy {
        PaidCardIssuanceState(
            screenStatus = screenStatus,
            euroIssuanceAmount = euroIssuanceAmount
        )
    }

}