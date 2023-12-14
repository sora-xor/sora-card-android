package jp.co.soramitsu.oauth.feature.kyc.result.verificationrejected

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.compose.ScreenStatus
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardCommonVerification
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PriceInteractor
import jp.co.soramitsu.oauth.common.navigation.engine.activityresult.api.SetActivityResult
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VerificationRejectedViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val kycRepository: KycRepository,
    private val setActivityResult: SetActivityResult,
    private val priceInteractor: PriceInteractor,
) : BaseViewModel() {

    private val _verificationRejectedScreenState = MutableStateFlow(
        VerificationRejectedScreenState(
            screenStatus = ScreenStatus.ERROR,
            kycFreeAttemptsCount = 0,
            isFreeAttemptsLeft = false,
            kycAttemptCostInEuros = "",
            reason = null,
            reasonDetails = null,
        ),
    )
    val verificationRejectedScreenState = _verificationRejectedScreenState.asStateFlow()

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_rejected_title,
                visibility = true,
                navIcon = R.drawable.ic_cross,
                actionLabel = R.string.log_out,
            ),
        )

        fetchKycAttemptInfo()
    }

    private fun fetchKycAttemptInfo() {
        viewModelScope.launch {
            runCatching {
                val reasons = kycRepository.getCachedKycResponse()
                val token = userSessionRepository.getAccessToken()
                val (actualKycAttemptsLeft, isKycAttemptsLeft) =
                    kycRepository.getFreeKycAttemptsInfo(token)
                        .getOrThrow().run { freeAttemptsCount to freeAttemptAvailable }
                val kycAttemptPrice = priceInteractor.calculateKycAttemptPrice()

                _verificationRejectedScreenState.value =
                    _verificationRejectedScreenState.value.copy(
                        screenStatus = ScreenStatus.READY_TO_RENDER,
                        kycFreeAttemptsCount = actualKycAttemptsLeft,
                        kycAttemptCostInEuros = kycAttemptPrice,
                        isFreeAttemptsLeft = if (reasons?.first == SoraCardCommonVerification.Retry) true else isKycAttemptsLeft,
                        reason = reasons?.second?.additionalDescription,
                        reasonDetails = reasons?.second?.rejectionReasons?.map { it.desc },
                    )
            }.onFailure {
                _verificationRejectedScreenState.value =
                    _verificationRejectedScreenState.value.copy(
                        screenStatus = ScreenStatus.ERROR,
                    )
            }
        }
    }

    override fun onToolbarAction() {
        super.onToolbarAction()
        try {
            viewModelScope.launch {
                userSessionRepository.logOutUser()
            }.invokeOnCompletion {
                setActivityResult.setResult(SoraCardResult.Logout)
            }
        } catch (e: Exception) {
            /* DO NOTHING */
        }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        viewModelScope.launch {
            setActivityResult.setResult(
                SoraCardResult.Success(
                    status = SoraCardCommonVerification.Rejected,
                ),
            )
        }
    }

    fun onTryAgain() {
        if (_verificationRejectedScreenState.value.isFreeAttemptsLeft) {
            mainRouter.openGetPrepared()
        }

        /* Will be available latter */
//        setActivityResult.setResult(SoraCardResult.NavigateTo(OutwardsScreen.BUY))
    }

    fun openTelegramSupport() {
        mainRouter.openSupportChat()
    }
}
