package jp.co.soramitsu.oauth.feature.verification.getmorexor.state

import jp.co.soramitsu.oauth.R

/* Removable if datasource or logic of choices is changed */
enum class XorPurchaseMethod(val methodStringRes: Int) {

    DEPOSIT_XOR(R.string.get_more_xor_dialog_deposit_option),
    SWAP_CRYPTO(R.string.get_more_xor_dialog_swap_option),
    BUY_WITH_EURO(R.string.get_more_xor_dialog_buy_option)

}