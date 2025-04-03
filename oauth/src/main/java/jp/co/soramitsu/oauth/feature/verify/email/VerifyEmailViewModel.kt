package jp.co.soramitsu.oauth.feature.verify.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.uiscreens.DialogAlertState
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.verify.Timer
import jp.co.soramitsu.oauth.feature.verify.email.model.VerifyEmailState
import jp.co.soramitsu.oauth.feature.verify.format
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val timer: Timer,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    private companion object {
        const val CHECK_EMAIL_VERIFIED_DELAY_IN_MILLISECONDS = 5000L
    }

    var state by mutableStateOf(VerifyEmailState())
        private set

    private var authCallback: OAuthCallback? = null

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_email_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        state = state.copy(
            resendLinkButtonState = state.resendLinkButtonState.copy(
                title = TextValue.StringRes(R.string.common_resend_link),
            ),
            changeEmailButtonState = state.changeEmailButtonState.copy(
                title = TextValue.StringRes(R.string.common_change_email),
            ),
        )

        setUpOtpResendTimer()
        startOtpResendTimer()
    }

    private fun setUpOtpResendTimer() {
        timer.setOnTickListener { millisUntilFinished ->
            state = state.copy(
                resendLinkButtonState = state.resendLinkButtonState.copy(
                    enabled = false,
                    timer = millisUntilFinished.format(),
                ),
            )
        }

        timer.setOnFinishListener {
            state = state.copy(
                resendLinkButtonState = state.resendLinkButtonState.copy(
                    enabled = true,
                    timer = null,
                ),
            )
        }
    }

    private fun startOtpResendTimer() {
        timer.start()
    }

    private val checkEmailVerifiedCallback = object : CheckEmailVerifiedCallback {
        override fun onEmailNotVerified() {
            viewModelScope.launch {
                delay(CHECK_EMAIL_VERIFIED_DELAY_IN_MILLISECONDS)
                checkVerificationStatus()
            }
        }

        override fun onSignInSuccessful() {
            authCallback?.onOAuthSucceed()
        }

        override fun onUserSignInRequired() {
            mainRouter.openEnterPhoneNumber(clearStack = true)
        }

        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            dialogState = DialogAlertState(
                title = TextValue.SimpleText(error.name),
                message = TextValue.SimpleText(error.description),
                dismissAvailable = true,
                onPositive = {
                    dialogState = null
                },
            )
        }
    }

    fun setArgs(email: String, autoEmailSend: Boolean, authCallback: OAuthCallback) {
        this.authCallback = authCallback
        state = state.copy(email = email, autoSentEmail = autoEmailSend)
        checkVerificationStatus()
    }

    private fun checkVerificationStatus() {
        viewModelScope.launch {
            pwoAuthClientProxy.checkEmailVerified(checkEmailVerifiedCallback)
        }
    }

    fun onResendLink() {
        viewModelScope.launch {
            loading(true)
            pwoAuthClientProxy.sendNewVerificationEmail(sendNewVerificationEmailCallback)
        }
    }

    private val sendNewVerificationEmailCallback = object : SendNewVerificationEmailCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            dialogState = DialogAlertState(
                title = TextValue.SimpleText(error.name),
                message = TextValue.SimpleText(error.description),
                dismissAvailable = true,
                onPositive = {
                    dialogState = null
                },
            )
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            loading(false)
            startOtpResendTimer()
            authCallback?.let {
                setArgs(email, autoEmailSent, it)
            }
        }

        override fun onUserSignInRequired() {
            loading(false)
            mainRouter.openEnterPhoneNumber(clearStack = true)
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            resendLinkButtonState = state.resendLinkButtonState.copy(loading = loading),
        )
    }

    fun onChangeEmail() {
        mainRouter.openChangeEmail()
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
