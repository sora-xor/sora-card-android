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
            isFreeAttemptsLeft = false,
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
                navIcon = R.drawable.ic_cross
            ),
        )

        fetchKycAttemptInfo()
    }

    private fun fetchKycAttemptInfo() {
        viewModelScope.launch {
            kotlin.runCatching {
                val token = userSessionRepository.getAccessToken()

                val (actualKycAttemptsLeft, isKycAttemptsLeft) = kycRepository.getFreeKycAttemptsInfo(token)
                    .getOrThrow().run { total - completed - rejected to freeAttemptAvailable }

                val kycAttemptPrice = priceInteractor.calculateKycAttemptPrice().getOrThrow()

                verificationRejectedScreenState = verificationRejectedScreenState.copy(
                    screenStatus = ScreenStatus.READY_TO_RENDER,
                    kycAttemptsCount = actualKycAttemptsLeft,
                    kycAttemptCostInEuros = kycAttemptPrice,
                    isFreeAttemptsLeft = isKycAttemptsLeft
                )
            }.onFailure {
                verificationRejectedScreenState = verificationRejectedScreenState.copy(
                    screenStatus = ScreenStatus.ERROR
                )
            }
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        setActivityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.MAIN_SCREEN))
    }

    fun onTryAgain() {
        if (verificationRejectedScreenState.kycAttemptsCount > 0) {
            mainRouter.openGetPrepared()
            return
        }

        /* Will be available latter */
//        setActivityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.BUY))
    }

    fun openTelegramSupport() {
        mainRouter.openSupportChat()
    }
}
