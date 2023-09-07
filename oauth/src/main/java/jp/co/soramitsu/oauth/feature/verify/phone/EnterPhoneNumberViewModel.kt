package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.verify.formatForAuth
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.model.EnterPhoneNumberState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterPhoneNumberViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
) : BaseViewModel() {

    companion object {
        const val PHONE_NUMBER_LENGTH_MAX = 16
        const val PHONE_NUMBER_LENGTH_MIN = 8
    }

    private val _state = MutableStateFlow(
        EnterPhoneNumberState(
            inputTextState = InputTextState(
                label = if (inMemoryRepo.environment == SoraCardEnvironmentType.TEST) {
                    "Use +12345678901 in this field"
                } else {
                    R.string.enter_phone_number_phone_input_field_label
                },
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = ButtonState(
                title = R.string.common_send_code,
                enabled = false,
            ),
        )
    )
    val state = _state.asStateFlow()

    private val requestOtpCallback = object : SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            getErrorMessage(error)?.let { descriptionText ->
                _state.value = _state.value.copy(
                    inputTextState = _state.value.inputTextState.copy(
                        error = true,
                        descriptionText = descriptionText
                    )
                )
            }
        }

        private fun getErrorMessage(errorCode: OAuthErrorCode): String? {
            return when (errorCode) {
                OAuthErrorCode.NO_INTERNET -> "Check your internet connection"
                OAuthErrorCode.INVALID_PHONE_NUMBER -> "Phone number is not valid"
                OAuthErrorCode.USER_IS_SUSPENDED -> "Phone number is suspended"
                else -> {
                    null
                }
            }
        }

        override fun onShowOtpInputScreen(otpLength: Int) {
            loading(false)
            mainRouter.openVerifyPhoneNumber(_state.value.inputTextState.value.text, otpLength)
        }
    }

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_phone_number_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
    }

    fun onPhoneChanged(value: TextFieldValue) {
        if (value.text.length > PHONE_NUMBER_LENGTH_MAX) {
            return
        }

        val numbers = value.copy(text = value.text.filter { it.isDigit() })

        _state.value = _state.value.copy(
            inputTextState = _state.value.inputTextState.copy(
                value = numbers,
                error = false,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = _state.value.buttonState.copy(enabled = numbers.text.isNotEmpty() && numbers.text.length >= PHONE_NUMBER_LENGTH_MIN)
        )
    }

    fun onRequestCode() {
        viewModelScope.launch {
            loading(true)
            pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(
                phoneNumber = _state.value.inputTextState.value.text.formatForAuth(),
                callback = requestOtpCallback,
            )
        }
    }

    private fun loading(loading: Boolean) {
        _state.value = _state.value.copy(
            buttonState = _state.value.buttonState.copy(loading = loading)
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
