package jp.co.soramitsu.oauth.feature.verification.rejected

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.impl.AccountInteractorImpl
import jp.co.soramitsu.oauth.theme.views.ScreenStatus
import jp.co.soramitsu.oauth.common.interactors.prices.api.PriceInteractor
import jp.co.soramitsu.oauth.common.interactors.user.api.UserInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationRejectedViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val priceInteractor: PriceInteractor,
    private val verificationFlow: VerificationFlow,
    private val accountInteractor: AccountInteractor
) : BaseViewModel() {

    var verificationRejectedScreenState by mutableStateOf(
        VerificationRejectedScreenState(
            screenStatus = ScreenStatus.ERROR,
            kycAttemptsCount = 0,
            kycAttemptCostInEuros = (-1).toDouble(),
            additionalInfo = ""
        )
    )
        private set

    init {
        verificationFlow.argsFlow.filter { (destination, _) ->
            destination is VerificationDestination.VerificationRejected
        }.onEach { (_, bundle) ->
            verificationRejectedScreenState = verificationRejectedScreenState.copy(
                additionalInfo = bundle.getString(
                    VerificationDestination.VerificationRejected.ADDITIONAL_INFO_KEY,
                    ""
                )
            )
        }.launchIn(viewModelScope)

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_rejected_title,
                visibility = true,
                actionLabel = "LogOut", // TODO change to string res
                navIcon = R.drawable.ic_cross
            ),
        )

        fetchKycAttemptInfo()
    }

    override fun onToolbarAction() {
        super.onToolbarAction()

        viewModelScope.launch {
            accountInteractor.logOut()
        }.invokeOnCompletion { verificationFlow.onLogout() }
    }

    override fun onToolbarNavigation() {
        super.onToolbarNavigation()
        verificationFlow.onExit()
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
