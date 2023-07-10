package jp.co.soramitsu.oauth.feature.verification.failed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
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
class VerificationFailedViewModel @Inject constructor(
    private val verificationFlow: VerificationFlow,
    private val accountInteractor: AccountInteractor
) : BaseViewModel() {

    var additionalInfo by mutableStateOf("")
        private set

    init {
        verificationFlow.argsFlow.filter { (destination, _) ->
            destination is VerificationDestination.VerificationFailed
        }.onEach { (_, bundle) ->
            additionalInfo = bundle.getString(
                VerificationDestination.VerificationFailed.ADDITIONAL_INFO_KEY,
                ""
            )
        }.launchIn(viewModelScope)

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verification_failed_title,
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

    fun onClose() {
        verificationFlow.onExit()
    }
}
