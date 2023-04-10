package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.feature.verify.formatForAuth
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.model.EnterPhoneNumberState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterPhoneNumberViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val inMemoryRepo: InMemoryRepo,
) : BaseViewModel() {

    private companion object {
        const val PHONE_NUMBER_LENGTH = 12
    }

    var state by mutableStateOf(
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
        private set

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
            mainRouter.openVerifyPhoneNumber(state.inputTextState.value.text, otpLength)
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
        if (value.text.length > PHONE_NUMBER_LENGTH) {
            return
        }

        val numbers = value.copy(text = value.text.filter { it.isDigit() })

        state = state.copy(
            inputTextState = state.inputTextState.copy(
                value = numbers,
                error = false,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = state.buttonState.copy(enabled = numbers.text.isNotEmpty())
        )
    }

    fun onRequestCode() {
        viewModelScope.launch {
            loading(true)
            PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
                phoneNumber = state.inputTextState.value.text.formatForAuth(),
                callback = requestOtpCallback
            )
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            buttonState = state.buttonState.copy(loading = loading)
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
