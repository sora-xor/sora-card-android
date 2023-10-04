package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.compose.Text

data class PaidCardIssuanceState(
    val screenStatus: ScreenStatus,
    val euroIssuanceAmount: String,
) {

    val titleText: Text
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_paid_card_title,
                    payload = arrayOf(euroIssuanceAmount)
                )

            return Text.StringRes(id = R.string.cant_fetch_data)
        }

    val descriptionText: Text =
        Text.StringRes(R.string.card_issuance_screen_paid_card_description)

    val isPayIssuanceAmountButtonEnabled: Boolean =
        screenStatus === ScreenStatus.READY_TO_RENDER

    val payIssuanceAmountText: Text
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_paid_card_pay_euro,
                    payload = arrayOf(euroIssuanceAmount)
                )

            return Text.StringRes(id = R.string.cant_fetch_data)
        }

}