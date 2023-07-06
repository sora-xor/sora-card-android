package jp.co.soramitsu.oauth.feature.registration.sendverificationemail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.theme.views.state.DialogAlertState
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.core.engines.timer.Timer
import jp.co.soramitsu.oauth.base.extension.format
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val timer: Timer,
    private val accountInteractor: AccountInteractor,
    private val registrationFlow: RegistrationFlow
) : BaseViewModel() {

    var state by mutableStateOf(VerifyEmailState())
        private set

    init {
        with(accountInteractor) {
            viewModelScope.launch {
                checkEmailVerificationStatus()
            }

            resultFlow.onEach {
                loading(false)
                dialogState = DialogAlertState(
                    title = it.text,
                    message = it.text,
                    dismissAvailable = true,
                    onPositive = {
                        dialogState = null
                    }
                )
            }.launchIn(viewModelScope)
        }

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_email_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        state = state.copy(
            resendLinkButtonState = state.resendLinkButtonState.copy(
                title = R.string.common_resend_link
            ),
            changeEmailButtonState = state.changeEmailButtonState.copy(
                title = R.string.common_change_email
            )
        )

        setUpOtpResendTimer()
        startOtpResendTimer()
    }

    private fun setUpOtpResendTimer() {
        timer.setOnTickListener { millisUntilFinished ->
            state = state.copy(
                resendLinkButtonState = state.resendLinkButtonState.copy(
                    enabled = false,
                    timer = millisUntilFinished.format()
                )
            )
        }

        timer.setOnFinishListener {
            state = state.copy(
                resendLinkButtonState = state.resendLinkButtonState.copy(
                    enabled = true,
                    timer = null
                )
            )
        }
    }

    private fun startOtpResendTimer() {
        timer.start()
    }

    override fun onToolbarNavigation() {
        registrationFlow.onExit()
    }

    fun onResendLink() {
        viewModelScope.launch {
            loading(true)
            accountInteractor.requestNewVerificationEmail()
        }
    }

    fun onChangeEmail() {
        // TODO change!!!
        registrationFlow.onEnterEmail(
            firstName = "",
            lastName = ""
        )
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            resendLinkButtonState = state.resendLinkButtonState.copy(loading = loading)
        )
    }
}
