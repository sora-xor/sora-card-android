package jp.co.soramitsu.oauth.feature.getmorexor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.common.navigation.flow.api.KycRequirementsUnfulfilledFlow
import jp.co.soramitsu.oauth.common.navigation.flow.api.NavigationFlow
import jp.co.soramitsu.oauth.feature.getmorexor.state.ChooseXorPurchaseMethodState
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod
import javax.inject.Inject

@HiltViewModel
class GetMoreXorViewModel @Inject constructor(
    @KycRequirementsUnfulfilledFlow private val kycRequirementsUnfulfilledFlow: NavigationFlow,
    private val setActivityResult: SetActivityResult
): ViewModel () {

    val choosePurchaseXorMethodState by mutableStateOf(
        ChooseXorPurchaseMethodState(
            xorPurchaseMethods = XorPurchaseMethod.entries,
        )
    )

    fun onPurchaseMethodClicked(methodId: Int) {
        choosePurchaseXorMethodState.xorPurchaseMethods[methodId]
            .mapToSoraCardNavigation().apply {
                setActivityResult.setResult(this)
            }
    }

    fun onCancelDialogClicked() {
        kycRequirementsUnfulfilledFlow.back()
    }

}