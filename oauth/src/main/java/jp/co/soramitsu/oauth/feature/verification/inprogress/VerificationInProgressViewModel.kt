package jp.co.soramitsu.oauth.feature.verification.inprogress

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.verification.api.VerificationFlow
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationInProgressViewModel @Inject constructor(
    private val verificationFlow: VerificationFlow,
    private val accountInteractor: AccountInteractor
) : DisposableViewModel() {

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.kyc_result_verification_in_progress,
                visibility = true,
                actionLabel = "LogOut", // TODO change to string res
                navIcon = R.drawable.ic_cross
            ),
        )
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

    fun openTelegramSupport() {
        verificationFlow.onOpenSupport()
    }
}
