package jp.co.soramitsu.oauth.feature.verification.cardissuance.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.theme.views.ScreenStatus
import jp.co.soramitsu.oauth.theme.views.Text

data class PaidCardIssuanceState(
    val screenStatus: ScreenStatus,
    val euroIssuanceAmount: Int
) {

    val titleText: Text
        get() {
            if (screenStatus === ScreenStatus.READY_TO_RENDER)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_paid_card_title,
                    payload = arrayOf(euroIssuanceAmount.toString())
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
                    payload = arrayOf(euroIssuanceAmount.toString())
                )

            return Text.StringRes(id = R.string.cant_fetch_data)
        }

}