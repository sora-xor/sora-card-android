package jp.co.soramitsu.oauth.feature.cardissuance.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text
import kotlin.math.absoluteValue

data class FreeCardIssuanceState(
    val xorCurrentAmount: Float,
    val xorInsufficientAmount: Float
) {

    val titleText: Text =
        Text.StringRes(
            id = R.string.card_issuance_screen_free_card_title
        )

    val descriptionText: Text =
        Text.StringRes(
            id = R.string.card_issuance_screen_free_card_description
        )

    val xorSufficiencyText: Text
        get() {
            if (xorInsufficientAmount > 0 && xorCurrentAmount >= 0)
                return Text.StringResWithArgs(
                    id = R.string.details_need_xor_desription,
                    payload = arrayOf(
                        xorInsufficientAmount.toString(),
                        xorCurrentAmount.toString()
                    )
                )

            if (xorInsufficientAmount > 0 && xorCurrentAmount < 0)
                return Text.StringResWithArgs(
                    id = R.string.details_need_xor_desription,
                    payload = arrayOf(
                        (xorCurrentAmount.absoluteValue + xorInsufficientAmount).toString(),
                        xorCurrentAmount.toString()
                    )
                )

            if (xorInsufficientAmount == 0f && xorCurrentAmount > 0)
                return Text.StringResWithArgs(
                    id = R.string.details_need_xor_desription,
                    payload = arrayOf(
                        xorInsufficientAmount.toString(),
                        xorCurrentAmount.toString()
                    )
                )

            return Text.StringRes(id = R.string.cant_fetch_data)
        }

    val xorSufficiencyPercentage: Float
        get() {
            if (xorInsufficientAmount > 0 && xorCurrentAmount >= 0)
                return xorInsufficientAmount / (xorCurrentAmount + xorInsufficientAmount)

            if (xorInsufficientAmount > 0 && xorCurrentAmount < 0)
                return xorInsufficientAmount / (xorCurrentAmount.absoluteValue + xorInsufficientAmount)

            if (xorInsufficientAmount == 0f && xorCurrentAmount > 0)
                return xorCurrentAmount / (xorCurrentAmount + xorInsufficientAmount)

            return 0f
        }

    val shouldGetInsufficientXorButtonBeShown: Boolean
        get() {
            if (xorInsufficientAmount > 0 && xorCurrentAmount >= 0)
                return true

            if (xorInsufficientAmount > 0 && xorCurrentAmount < 0)
                return true

            if (xorInsufficientAmount == 0f && xorCurrentAmount > 0)
                return true

            return false
        }

    val getInsufficientXorText: Text
        get() {
            if (xorInsufficientAmount > 0 && xorCurrentAmount >= 0)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_free_card_get_xor,
                    payload = arrayOf(xorInsufficientAmount.toString())
                )

            if (xorInsufficientAmount > 0 && xorCurrentAmount < 0)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_free_card_get_xor,
                    payload = arrayOf((xorCurrentAmount.absoluteValue + xorInsufficientAmount).toString())
                )

            if (xorInsufficientAmount == 0f && xorCurrentAmount > 0)
                return Text.StringResWithArgs(
                    id = R.string.card_issuance_screen_free_card_get_xor,
                    payload = arrayOf(xorInsufficientAmount.toString())
                )

            return Text.StringRes(id = R.string.card_issuance_screen_free_card_get_xor,)
        }

}