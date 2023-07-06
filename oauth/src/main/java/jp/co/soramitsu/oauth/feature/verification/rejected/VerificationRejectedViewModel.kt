package jp.co.soramitsu.oauth.feature.verification.rejected

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.theme.views.ScreenStatus
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationRejectedViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val priceInteractor: PriceInteractor,
    private val verificationFlow: VerificationFlow,
) : BaseViewModel() {

    var verificationRejectedScreenState by mutableStateOf(
        VerificationRejectedScreenState(
            screenStatus = ScreenStatus.ERROR,
            kycAttemptsCount = 0,
            kycAttemptCostInEuros = (-1).toDouble()
        )
    )
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_rejected_title,
                visibility = true,
                navIcon = null,
            ),
        )

        fetchKycAttemptInfo()
    }

    private fun fetchKycAttemptInfo() {
        viewModelScope.launch {
            kotlin.runCatching {
                val kycAttemptsLeft = userInteractor.calculateFreeKycAttemptsLeft().getOrThrow()

                val kycAttemptPrice = priceInteractor.calculateKycAttemptPrice().getOrThrow()

                verificationRejectedScreenState = verificationRejectedScreenState.copy(
                    screenStatus = ScreenStatus.READY_TO_RENDER,
                    kycAttemptsCount = kycAttemptsLeft,
                    kycAttemptCostInEuros = kycAttemptPrice
                )
            }.onFailure {
                it.printStackTrace()
                verificationRejectedScreenState = verificationRejectedScreenState.copy(
                    screenStatus = ScreenStatus.ERROR
                )
            }
        }
    }

    fun onTryAgain() {
        verificationFlow.onTryAgain()
    }

    fun openTelegramSupport() {
        verificationFlow.onOpenSupport()
    }
}
