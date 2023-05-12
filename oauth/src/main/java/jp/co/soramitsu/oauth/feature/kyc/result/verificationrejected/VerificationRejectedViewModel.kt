package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.OutwardsScreen
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationRejectedViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
    private val setActivityResult: SetActivityResult,
    private val priceInteractor: PriceInteractor
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
                val token = userSessionRepository.getAccessToken()

                val kycAttemptsLeft = kycRepository.getFreeKycAttemptsInfo(token)
                    .getOrThrow().run { total - completed }

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
        if (verificationRejectedScreenState.kycAttemptsCount > 0) {
            mainRouter.openGetPrepared()
            return
        }

        setActivityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.BUY))
    }

    fun openTelegramSupport() {
        mainRouter.openSupportChat()
    }
}
