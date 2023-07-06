package jp.co.soramitsu.oauth.feature.verify.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.verify.email.model.EnterEmailState
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterEmailViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val userSessionRepository: UserSessionRepository,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    var state by mutableStateOf(
        EnterEmailState(
            inputTextState = InputTextState(
                label = R.string.enter_email_input_field_label,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = ButtonState(
                title = R.string.common_send_link,
                enabled = false,
            )
        )
    )
        private set

    private var firstName: String = ""
    private var lastName: String = ""

    private var authCallback: OAuthCallback? = null

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.enter_email_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
    }

    private val registerUserCallback = object : RegisterUserCallback {
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

        private fun getErrorMessage(errorCode: OAuthErrorCode): String? {
            return when (errorCode) {
                OAuthErrorCode.NO_INTERNET -> "Check your internet connection"
                OAuthErrorCode.USER_IS_SUSPENDED -> "Phone number is suspended"
                OAuthErrorCode.INVALID_EMAIL -> "Email is not a valid email!"
                else -> null
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            loading(false)
            mainRouter.openVerifyEmail(email, autoEmailSent, clearStack = true)
        }

        override fun onSignInSuccessful(
            refreshToken: String,
            accessToken: String,
            accessTokenExpirationTime: Long
        ) {
            viewModelScope.launch {
                userSessionRepository.signInUser(
                    refreshToken,
                    accessToken,
                    accessTokenExpirationTime
                )
                authCallback?.onOAuthSucceed(accessToken)
            }
        }

        override fun onUserSignInRequired() {
            mainRouter.openEnterPhoneNumber(clearStack = true)
        }
    }

    fun setArgs(firstName: String?, lastName: String?, authCallback: OAuthCallback) {
        firstName?.let {
            this.firstName = it
        }

        lastName?.let {
            this.lastName = it
        }
        this.authCallback = authCallback
    }

    fun onEmailChanged(value: TextFieldValue) {
        state = state.copy(
            inputTextState = state.inputTextState.copy(
                value = value,
                error = false,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = state.buttonState.copy(enabled = value.text.isNotEmpty())
        )
    }

    fun onRegisterUser() {
        viewModelScope.launch {
            loading(true)
            pwoAuthClientProxy.registerUser(
                firstName = firstName,
                lastName = lastName,
                email = state.inputTextState.value.text,
                callback = registerUserCallback,
            )
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            inputTextState = state.inputTextState.copy(enabled = !loading),
            buttonState = state.buttonState.copy(loading = loading)
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
