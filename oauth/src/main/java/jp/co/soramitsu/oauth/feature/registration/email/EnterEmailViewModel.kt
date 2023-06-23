package jp.co.soramitsu.oauth.feature.registration.email

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.feature.OAuthCallback
import jp.co.soramitsu.oauth.core.datasources.session.api.UserSessionRepository
import jp.co.soramitsu.oauth.feature.registration.email.model.EnterEmailState
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterEmailViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val registrationFlow: RegistrationFlow
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

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.enter_email_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )

        accountInteractor.resultFlow.onEach {
            state = state.copy(
                inputTextState = state.inputTextState.copy(
                    error = true,
                    descriptionText = it.text
                )
            )
        }.launchIn(viewModelScope)
    }

    override fun onToolbarNavigation() {
        registrationFlow.onBack()
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
            accountInteractor.registerUser(
                firstName = firstName,
                lastName = lastName,
                email = state.inputTextState.value.text,
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
