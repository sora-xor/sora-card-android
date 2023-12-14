package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text

data class FreeCardIssuanceState(
    val screenStatus: ScreenStatus,
    val xorInsufficientAmount: Double,
    val euroInsufficientAmount: Double,
    val euroLiquidityThreshold: Double,
) {

    val titleText: Text =
        Text.StringRes(
            id = R.string.card_issuance_screen_free_card_title,
        )

    val descriptionText: Text =
        Text.StringResWithArgs(
            id = R.string.card_issuance_screen_free_card_description,
            payload = arrayOf(euroLiquidityThreshold.toInt().toString()),
        )

    val xorSufficiencyText: Text
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return Text.StringResWithArgs(
                    id = R.string.details_need_xor_desription,
                    payload = arrayOf(
                        String.format("%.2f", xorInsufficientAmount),
                        String.format("%.2f", euroInsufficientAmount),
                    ),
                )
            }

            return Text.StringRes(id = R.string.cant_fetch_data)
        }

    val xorSufficiencyPercentage: Float
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return (euroLiquidityThreshold.minus(euroInsufficientAmount))
                    .div(euroLiquidityThreshold).toFloat()
            }

            return 0f
        }

    val isGetInsufficientXorButtonEnabled: Boolean =
        screenStatus === ScreenStatus.READY_TO_RENDER

    val getInsufficientXorText: Text
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_free_card_get_xor,
                    payload = arrayOf(String.format("%.2f", xorInsufficientAmount)),
                )
            }

            return Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor)
        }
}
