package jp.co.soramitsu.oauth.feature.verify.phone

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import jp.co.soramitsu.androidfoundation.format.unsafeCast
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.base.navigation.SetActivityResult
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardFlow
import jp.co.soramitsu.oauth.base.sdk.contract.SoraCardResult
import jp.co.soramitsu.oauth.common.domain.KycRepository
import jp.co.soramitsu.oauth.common.domain.PWOAuthClientProxy
import jp.co.soramitsu.oauth.feature.session.domain.UserSessionRepository
import jp.co.soramitsu.oauth.feature.telephone.LocaleService
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.feature.verify.phone.model.EnterPhoneNumberState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EnterPhoneNumberViewModel @Inject constructor(
    private val mainRouter: MainRouter,
    private val pwoAuthClientProxy: PWOAuthClientProxy,
    private val localeService: LocaleService,
    private val kycRepository: KycRepository,
    private val setActivityResult: SetActivityResult,
    private val inMemoryRepo: InMemoryRepo,
    private val userSessionRepository: UserSessionRepository,
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
                label = R.string.enter_phone_number_phone_input_field_label,
                descriptionText = R.string.common_no_spam,
            ),
            buttonState = ButtonState(
                title = R.string.common_send_code,
                enabled = false,
            ),
            countryCode = "",
            countryName = "",
            countryLoading = true,
        ),
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
                    ),
                )
            }
        }
    }

    private val requestOtpCallback = object : SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            loading(false)
            (error.description.takeIf { it.isNotEmpty() } ?: errorMessage)?.let { descriptionText ->
                _state.value = _state.value.copy(
                    inputTextStateNumber = _state.value.inputTextStateNumber.copy(
                        error = true,
                        descriptionText = descriptionText,
                    ),
                )
            }
        }

        override fun onShowTimeBasedOtpVerificationInputScreen(accountName: String) {
        }

        override fun onShowOtpInputScreen(otpLength: Int) {
            loading(false)
            viewModelScope.launch(Dispatchers.Main) {
                mainRouter.openVerifyPhoneNumber(
                    country = getPhoneCode(),
                    phoneNumber = _state.value.inputTextStateNumber.value.text,
                    otpLength = otpLength,
                )
            }
        }
    }

    init {
        mToolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_phone_number_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
    }

    fun onPhoneChanged(value: TextFieldValue) {
        if (inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn.not() && (
                value.text.getOrNull(0)?.let {
                    it == '0'
                } == true
                )
        ) {
            return
        }
        if (getPhoneCode().length + value.text.length > PHONE_NUMBER_LENGTH_MAX) {
            return
        }

        val numbers = value.copy(text = value.text.filter { it.isDigit() })

        _state.value = _state.value.copy(
            inputTextStateNumber = _state.value.inputTextStateNumber.copy(
                value = numbers,
                error = false,
                descriptionText = if (inMemoryRepo.flow!!.unsafeCast<SoraCardFlow.SoraCardKycFlow>().logIn && value.text.startsWith("0")) R.string.phone_number_leading_zero else R.string.common_no_spam,
            ),
            buttonState = _state.value.buttonState.copy(enabled = numbers.text.isNotEmpty() && getPhoneCode().length + numbers.text.length >= PHONE_NUMBER_LENGTH_MIN),
        )
    }

    private var requestOtpAttempts: Long = 0

    fun onSelectCountry() {
        mainRouter.openCountryList(singleChoice = true)
    }

    fun onRequestCode() {
        viewModelScope.launch {
            loading(true)
            delay(1000 * 30 * requestOtpAttempts)
            requestOtpAttempts++
            userSessionRepository.setPhoneNumber(
                getPhoneCode() + _state.value.inputTextStateNumber.value.text,
            )
            pwoAuthClientProxy.signInWithPhoneNumberRequestOtp(
                countryCode = getPhoneCode(),
                phoneNumber = _state.value.inputTextStateNumber.value.text,
                callback = requestOtpCallback,
            )
        }
    }

    private fun getPhoneCode() = _state.value.inputTextStateCode.value.text.substring(1)

    private fun loading(loading: Boolean) {
        _state.value = _state.value.copy(
            buttonState = _state.value.buttonState.copy(loading = loading),
        )
    }

    override fun onToolbarNavigation() {
        setActivityResult.setResult(SoraCardResult.Canceled)
    }
}
