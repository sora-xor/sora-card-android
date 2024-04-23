package jp.co.soramitsu.oauth.feature.getmorexor.state

import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R

data class ChooseXorPurchaseMethodState(
    val xorPurchaseMethods: List<XorPurchaseMethod>,
) {

    val titleText: TextValue =
        TextValue.StringRes(id = R.string.details_get_more_xor)

    val descriptionText: TextValue =
        TextValue.StringRes(id = R.string.get_more_xor_dialog_description)

    val methodsTextList: List<TextValue> =
        xorPurchaseMethods.map { TextValue.StringRes(id = it.methodStringRes) }

    val cancelText: TextValue =
        TextValue.StringRes(id = R.string.common_cancel)
}
