package jp.co.soramitsu.oauth.feature.getmorexor.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Text

data class ChooseXorPurchaseMethodState(
    val xorPurchaseMethods: List<XorPurchaseMethod>,
) {

    val titleText: Text =
        Text.StringRes(id = R.string.details_get_more_xor)

    val descriptionText: Text =
        Text.StringRes(id = R.string.get_more_xor_dialog_description)

    val methodsTextList: List<Text> =
        xorPurchaseMethods.map { Text.StringRes(id = it.methodStringRes) }

    val cancelText: Text =
        Text.StringRes(id = R.string.common_cancel)
}
