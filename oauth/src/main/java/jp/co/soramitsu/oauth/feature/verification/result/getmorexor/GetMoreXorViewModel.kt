package jp.co.soramitsu.oauth.feature.verification.result.getmorexor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.state.ChooseXorPurchaseMethodState
import jp.co.soramitsu.oauth.feature.verification.result.getmorexor.state.XorPurchaseMethod
import javax.inject.Inject

@HiltViewModel
class GetMoreXorViewModel @Inject constructor(
    private val verificationFlow: VerificationFlow
): ViewModel () {

    val choosePurchaseXorMethodState by mutableStateOf(
        ChooseXorPurchaseMethodState(
            xorPurchaseMethods = XorPurchaseMethod.values().toList(),
        )
    )

    fun onPurchaseMethodClicked(methodId: Int) =
        when(choosePurchaseXorMethodState.xorPurchaseMethods[methodId]) {
            XorPurchaseMethod.DEPOSIT_XOR -> verificationFlow.onDepositMoreXor()
            XorPurchaseMethod.BUY_WITH_EURO -> verificationFlow.onBuyMoreXor()
            XorPurchaseMethod.SWAP_CRYPTO -> verificationFlow.onSwapMoreCrypto()
        }

    fun onCancelDialogClicked() {
        verificationFlow.onBack()
    }

}