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
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.telephone.LocaleService
import jp.co.soramitsu.oauth.feature.verify.formatForAuth
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.model.EnterPhoneNumberState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterPhoneNumberViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    inMemoryRepo: InMemoryRepo,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    private val localeService: LocaleService,
    private val kycRepository: KycRepository,
) : BaseViewModel() {

    companion object {
        const val PHONE_NUMBER_LENGTH_MAX = 16
        const val PHONE_NUMBER_LENGTH_MIN = 8
    }

    private val _state = MutableStateFlow(
        EnterPhoneNumberState(
            inputTextStateCode = InputTextState(
                value = TextFieldValue(""),
                label = null,
                descriptionText = null,
            ),
            inputTextStateNumber = InputTextState(
                value = TextFieldValue(""),
                label = if (inMemoryRepo.environment == SoraCardEnvironmentType.TEST) "Use 12346578 in this field" else R.string.enter_phone_number_phone_input_field_label,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = ButtonState(
                title = R.string.common_send_code,
                enabled = false,
            ),
            countryCode = "",
            countryName = "",
            countryLoading = true,
        )
    )
    val state = _state.asStateFlow()

    fun setLocale(locale: String?) {
        viewModelScope.launch {
            val countries = kycRepository.getCountries()
            if (countries.isEmpty()) {
                _state.value = _state.value.copy(
                    countryLoading = false,
                )
            } else {
                val cur = localeService.code
                val country = countries.find { it.code.lowercase() == (locale ?: cur).lowercase() }
                    ?: countries[0]
                _state.value = _state.value.copy(
                    countryLoading = false,
                    countryCode = country.code,
                    countryName = country.name,
                    inputTextStateCode = _state.value.inputTextStateCode.copy(
                        value = TextFieldValue(country.dialCode),
                    )
                )
            }
        }
    }

    private val requestOtpCallback = object : SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            getErrorMessage(error)?.let { descriptionText ->
                _state.value = _state.value.copy(
                    inputTextStateNumber = _state.value.inputTextStateNumber.copy(
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
            mainRouter.openVerifyPhoneNumber(
                getPhoneCode() + _state.value.inputTextStateNumber.value.text,
                otpLength
            )
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
        if (value.text.getOrNull(0)?.let { it == '0' } == true) return
        if (getPhoneCode().length + value.text.length > PHONE_NUMBER_LENGTH_MAX) {
            return
        }

        val numbers = value.copy(text = value.text.filter { it.isDigit() })

        _state.value = _state.value.copy(
            inputTextStateNumber = _state.value.inputTextStateNumber.copy(
                value = numbers,
                error = false,
            ),
            buttonState = _state.value.buttonState.copy(enabled = numbers.text.isNotEmpty() && getPhoneCode().length + numbers.text.length >= PHONE_NUMBER_LENGTH_MIN)
        )
    }

    private var requestOtpAttempts: Long = 0

    fun onSelectCountry() {
        mainRouter.openCountryList()
    }

    fun onRequestCode() {
        viewModelScope.launch {
            loading(true)
            delay(1000 * 15 * requestOtpAttempts)
            requestOtpAttempts++
            pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(
                phoneNumber = (getPhoneCode() + _state.value.inputTextStateNumber.value.text).formatForAuth(),
                callback = requestOtpCallback,
            )
        }
    }

    private fun getPhoneCode() = _state.value.inputTextStateCode.value.text.substring(1)

    private fun loading(loading: Boolean) {
        _state.value = _state.value.copy(
            buttonState = _state.value.buttonState.copy(loading = loading)
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
