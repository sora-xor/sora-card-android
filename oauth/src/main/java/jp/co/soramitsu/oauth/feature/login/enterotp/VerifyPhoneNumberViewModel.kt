package jp.co.soramitsu.oauth.feature.login.enterotp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.core.engines.timer.Timer
import jp.co.soramitsu.oauth.base.extension.format
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginDestination
import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.oauth.theme.views.obtainStringAsAny
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneNumberViewModel @Inject constructor(
    inMemoryRepo: InMemoryRepo,
    private val timer: Timer,
    private val loginFlow: LoginFlow,
    private val accountInteractor: AccountInteractor
) : DisposableViewModel() {

    var state by mutableStateOf(
        VerifyPhoneNumberState(
            otpLength = Int.MAX_VALUE,
            inputTextState = InputTextState(
                label = if (inMemoryRepo.environment == SoraCardEnvironmentType.TEST) {
                    "Use 123456 in this field"
                } else {
                    R.string.verify_phone_number_code_input_field_label
                }
            ),
            buttonState = ButtonState(
                title = R.string.common_resend_code,
                enabled = false,
            )
        )
    )
        private set

    init {
        loginFlow.argsFlow.filter { (destination, _) ->
            destination is LoginDestination.EnterOtp
        }.onEach { (_, bundle) ->
            state = state.copy(
                otpLength = bundle.getInt(
                    LoginDestination.EnterOtp.OTP_LENGTH_KEY,
                    Int.MAX_VALUE
                )
            )
        }.launchIn(viewModelScope)

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_phone_number_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        setUpOtpResendTimer()
        startOtpResendTimer()
    }

    private fun setUpOtpResendTimer() {
        timer.setOnTickListener { millisUntilFinished ->
            state = state.copy(
                buttonState = state.buttonState.copy(
                    enabled = false,
                    timer = millisUntilFinished.format()
                )
            )
        }

        timer.setOnFinishListener {
            state = state.copy(
                buttonState = state.buttonState.copy(
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
        loginFlow.onBack()
    }

    private var disposableJob: Job? = null

    override fun onStart() {
        super.onStart()
        disposableJob = accountInteractor.resultFlow
            .onEach { result ->
                when(result) {
                    is AccountOperationResult.Executed -> {
                        loading(false)
                    }
                    is AccountOperationResult.Loading -> {
                        /* DO NOTHING */
                    }
                    is AccountOperationResult.Error -> {
                        loading(false)
                        state = state.copy(
                            inputTextState = state.inputTextState.copy(
                                error = true,
                                descriptionText = result.text.obtainStringAsAny()
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    override fun onStop() {
        super.onStop()
        disposableJob?.cancel()
        disposableJob = null
    }

    fun onCodeChanged(value: TextFieldValue) {
        if (value.text.length > state.otpLength) {
            return
        }

        if (value.text.length == state.otpLength && value.text != state.inputTextState.value.text) {
            viewModelScope.launch {
                loading(true)
                delay(LOADING_DELAY)
                accountInteractor.verifyOtpCode(state.inputTextState.value.text)
            }
        }

        state = state.copy(
            inputTextState = state.inputTextState.copy(
                value = value,
                error = false,
                descriptionText = ""
            )
        )
    }

    fun resendOtp() {
        viewModelScope.launch {
            loading(true)
            delay(LOADING_DELAY)
            accountInteractor.resendOtpCode()
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            inputTextState = state.inputTextState.copy(enabled = !loading),
            buttonState = state.buttonState.copy(loading = loading)
        )
    }

    private companion object {
        const val LOADING_DELAY = 300L
    }
}


//if (inMemoryRepo.mode != Mode.REGISTRATION) {
//    dialogState = DialogAlertState(
//        title = null,
//        message = R.string.common_user_not_found,
//        dismissAvailable = false,
//        onPositive = {
//            dialogState = null
//            finishWithError(SoraCardError.USER_NOT_FOUND)
//        }
//    )
//} else {
//    mainRouter.openVerifyEmail(email, autoEmailSent)
//}
