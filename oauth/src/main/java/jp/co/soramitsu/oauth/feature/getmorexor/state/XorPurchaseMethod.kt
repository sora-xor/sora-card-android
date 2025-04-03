package jp.co.soramitsu.oauth.feature.getmorexor.state

import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult

/* Removable if datasource or logic of choices is changed */
enum class XorPurchaseMethod(val methodStringRes: Int) {

    DEPOSIT_XOR(R.string.get_more_xor_dialog_deposit_option),
    SWAP_CRYPTO(R.string.get_more_xor_dialog_swap_option),
    ;

    //    BUY_WITH_EURO(R.string.get_more_xor_dialog_buy_option);

    fun mapToSoraCardNavigation(): SoraCardResult = when (this) {
        DEPOSIT_XOR -> SoraCardResult.NavigateTo(OutwardsScreen.DEPOSIT)
        SWAP_CRYPTO -> SoraCardResult.NavigateTo(OutwardsScreen.SWAP)
//        BUY_WITH_EURO -> SoraCardResult.NavigateTo(OutwardsScreen.BUY)
    }
}
