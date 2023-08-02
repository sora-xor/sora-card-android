package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.state.DialogAlertState
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.verify.Timer
import jp.co.soramitsu.oauth.feature.verify.format
import jp.co.soramitsu.oauth.feature.verify.formatForAuth
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.model.VerifyPhoneNumberState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneNumberViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val timer: Timer,
    inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    private companion object {
        const val LOADING_DELAY = 300L
    }

    var state by mutableStateOf(
        VerifyPhoneNumberState(
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

    private var phoneNumber: String = ""
    private var otpLength: Int = Int.MAX_VALUE

    private var authCallback: OAuthCallback? = null

    init {
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

    fun setArgs(
        phoneNumber: String?,
        otpLength: Int?,
        authCallback: OAuthCallback,
    ) {
        phoneNumber?.let {
            this.phoneNumber = it
        }
        otpLength?.let {
            this.otpLength = it
        }
        this.authCallback = authCallback
    }

    private val verifyOtpCallback = object : SignInWithPhoneNumberVerifyOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            getErrorMessage(error)?.let { descriptionText ->
                state = state.copy(
                    inputTextState = state.inputTextState.copy(
                        error = true,
                        descriptionText = descriptionText
                    )
                )
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            loading(false)
            mainRouter.openVerifyEmail(email, autoEmailSent)
        }

        override fun onShowRegistrationScreen() {
            loading(false)
            mainRouter.openRegisterUser()
        }

        override fun onSignInSuccessful(
            refreshToken: String,
            accessToken: String,
            accessTokenExpirationTime: Long
        ) {
            dialogState = DialogAlertState(
                title = "onSignInSuccessful",
                message = accessToken,
                dismissAvailable = false,
                onPositive = {
                    viewModelScope.launch {
                        signInUser(refreshToken, accessToken, accessTokenExpirationTime)
                        authCallback?.onOAuthSucceed(accessToken)
                    }
                    dialogState = null
                }
            )
        }

        override fun onUserSignInRequired() {
            dialogState = DialogAlertState(
                title = "onUserSignInRequired",
                message = "onUserSignInRequired",
                dismissAvailable = false,
                onPositive = {
                    resendOtp()
                    dialogState = null
                }
            )
//            resendOtp()
        }

        override fun onVerificationFailed() {
            loading(false)
            state = state.copy(
                inputTextState = state.inputTextState.copy(
                    error = true,
                    descriptionText = "OTP is not valid"
                )
            )
        }
    }

    private suspend fun signInUser(
        refreshToken: String,
        accessToken: String,
        accessTokenExpirationTime: Long
    ) {
        userSessionRepository.signInUser(
            refreshToken,
            accessToken,
            accessTokenExpirationTime
        )
    }

    private val requestOtpCallback = object : SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            getErrorMessage(error)?.let { descriptionText ->
                state = state.copy(
                    inputTextState = state.inputTextState.copy(
                        error = true,
                        descriptionText = descriptionText
                    )
                )
            }
        }

        override fun onShowOtpInputScreen(otpLength: Int) {
            loading(false)
            startOtpResendTimer()
        }
    }

    private fun getErrorMessage(errorCode: OAuthErrorCode): String {
        return errorCode.description
    }
//    private fun getErrorMessage(errorCode: OAuthErrorCode): String? {
//        return when (errorCode) {
//            OAuthErrorCode.NO_INTERNET -> "Check your internet connection"
//            OAuthErrorCode.USER_IS_SUSPENDED -> "Phone number is suspended"
//            else -> null
//        }
//    }

    fun onCodeChanged(value: TextFieldValue) {
        if (value.text.length > otpLength) {
            return
        }

        if (value.text.length == otpLength && value.text != state.inputTextState.value.text) {
            verifyOtp()
        }

        state = state.copy(
            inputTextState = state.inputTextState.copy(
                value = value,
                error = false,
                descriptionText = ""
            )
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            loading(true)
            delay(LOADING_DELAY)
            pwoAuthClientProxy.signInWithPhoneNumberVerifyOtp(
                otp = state.inputTextState.value.text,
                callback = verifyOtpCallback,
            )
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            loading(true)
            delay(LOADING_DELAY)
            pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(
                phoneNumber = phoneNumber.formatForAuth(),
                callback = requestOtpCallback,
            )
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            inputTextState = state.inputTextState.copy(enabled = !loading),
            buttonState = state.buttonState.copy(loading = loading)
        )
    }
}
