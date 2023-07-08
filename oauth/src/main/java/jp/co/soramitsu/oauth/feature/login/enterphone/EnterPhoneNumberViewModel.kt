package jp.co.soramitsu.oauth.feature.login.enterphone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.sdk.InMemoryRepo
import jp.co.soramitsu.oauth.base.sdk.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.login.api.LoginFlow
import jp.co.soramitsu.oauth.base.extension.formatForAuth
import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterPhoneNumberViewModel @Inject constructor(
    private val inMemoryRepo: InMemoryRepo,
    private val accountInteractor: AccountInteractor,
    private val loginFlow: LoginFlow
) : BaseViewModel() {

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

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.verify_phone_number_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        accountInteractor.resultFlow
            .onEach {
//                loading(false)
//                state = state.copy(
//                    inputTextState = state.inputTextState.copy(
//                        error = true,
//                        descriptionText = it.text
//                    )
//                )
            }.launchIn(viewModelScope)
    }

    override fun onToolbarNavigation() {
        loginFlow.onBack()
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
            accountInteractor.requestOtpCode(
                phoneNumber = state.inputTextState.value.text.formatForAuth()
            )
        }
    }

    private fun loading(loading: Boolean) {
        state = state.copy(
            buttonState = state.buttonState.copy(loading = loading)
        )
    }

    private companion object {
        const val PHONE_NUMBER_LENGTH = 12
    }
}
