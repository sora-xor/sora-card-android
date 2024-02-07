package jp.co.soramitsu.oauth.feature.getmorexor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.feature.getmorexor.state.ChooseXorPurchaseMethodState
import jp.co.soramitsu.oauth.feature.getmorexor.state.XorPurchaseMethod

@HiltViewModel
class GetMoreXorViewModel @Inject constructor(
    private val setActivityResult: SetActivityResult,
    private val mainRouter: MainRouter,
) : ViewModel() {

    val choosePurchaseXorMethodState by mutableStateOf(
        ChooseXorPurchaseMethodState(
            xorPurchaseMethods = XorPurchaseMethod.entries,
        ),
    )

    fun onPurchaseMethodClicked(methodId: Int) {
        choosePurchaseXorMethodState.xorPurchaseMethods[methodId]
            .mapToSoraCardNavigation().apply {
                setActivityResult.setResult(this)
            }
    }

    fun onCancelDialogClicked() {
        mainRouter.back()
    }
}
