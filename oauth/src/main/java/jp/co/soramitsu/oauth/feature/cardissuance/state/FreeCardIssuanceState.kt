package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus

data class FreeCardIssuanceState(
    val screenStatus: ScreenStatus,
    val xorInsufficientAmount: Double,
    val euroInsufficientAmount: Double,
    val euroLiquidityThreshold: Double,
) {

    val titleText: TextValue =
        TextValue.StringRes(
            id = R.string.card_issuance_screen_free_card_title,
        )

    val descriptionText: TextValue =
        TextValue.StringResWithArgs(
            id = R.string.card_issuance_screen_free_card_description,
            payload = arrayOf(euroLiquidityThreshold.toInt().toString()),
        )

    val xorSufficiencyText: TextValue
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return TextValue.StringResWithArgs(
                    id = R.string.details_need_xor_desription,
                    payload = arrayOf(
                        String.format("%.2f", xorInsufficientAmount),
                        String.format("%.2f", euroInsufficientAmount),
                    ),
                )
            }

            return TextValue.StringRes(id = R.string.cant_fetch_data)
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

    val getInsufficientXorText: TextValue
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return TextValue.StringResWithArgs(
                    id = R.string.card_issuance_screen_free_card_get_xor,
                    payload = arrayOf(String.format("%.2f", xorInsufficientAmount)),
                )
            }

            return TextValue.StringRes(id = R.string.card_issuance_screen_free_card_get_xor)
        }
}
