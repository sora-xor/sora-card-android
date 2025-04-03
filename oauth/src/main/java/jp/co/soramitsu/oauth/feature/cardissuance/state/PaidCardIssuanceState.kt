package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.compose.ScreenStatus

data class PaidCardIssuanceState(
    val screenStatus: ScreenStatus,
    val euroIssuanceAmount: String,
) {

    val titleText: TextValue
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return TextValue.StringResWithArgs(
                    id = R.string.card_issuance_screen_paid_card_title,
                    payload = arrayOf(euroIssuanceAmount),
                )
            }

            return TextValue.StringRes(id = R.string.cant_fetch_data)
        }

    val descriptionText: TextValue =
        TextValue.StringRes(R.string.card_issuance_screen_paid_card_description)

    val isPayIssuanceAmountButtonEnabled: Boolean =
        screenStatus === ScreenStatus.READY_TO_RENDER

    val payIssuanceAmountText: TextValue
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER) {
                return TextValue.StringResWithArgs(
                    id = R.string.card_issuance_screen_paid_card_pay_euro,
                    payload = arrayOf(euroIssuanceAmount),
                )
            }

            return TextValue.StringRes(id = R.string.cant_fetch_data)
        }
}
