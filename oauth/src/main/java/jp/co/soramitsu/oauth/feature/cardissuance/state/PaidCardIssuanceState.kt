package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text

data class PaidCardIssuanceState(
    val issuanceAmount: Int
) {

    val titleText: Text
        get() {
            if (issuanceAmount <= 0)
                return Text.StringRes(id = R.string.cant_fetch_data)

            return Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_title,
                payload = arrayOf(issuanceAmount.toString())
            )
        }

    val descriptionText: Text =
        Text.StringRes(R.string.card_issuance_screen_paid_card_description)

    val shouldPayIssuanceAmountButtonBeEnabled: Boolean =
        issuanceAmount > 0

    val payIssuanceAmountText: Text
        get() {
            if (issuanceAmount <= 0)
                return Text.StringRes(id = R.string.cant_fetch_data)

            return Text.StringResWithArgs(
                id = R.string.card_issuance_screen_paid_card_pay_euro,
                payload = arrayOf(issuanceAmount.toString())
            )
        }

}