package jp.co.soramitsu.oauth.feature.registration.enternames

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val registrationFlow: RegistrationFlow
) : DisposableViewModel() {

    var state by mutableStateOf(
        RegisterUserState(
            firstNameState = InputTextState(
                label = R.string.user_registration_first_name_input_filed_label
            ),
            lastNameState = InputTextState(
                label = R.string.user_registration_last_name_input_filed_label
            ),
            buttonState = ButtonState(
                title = R.string.common_continue,
                enabled = false,
            )
        )
    )
        private set

    init {
        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.user_registration_title,
                visibility = true,
                actionLabel = "LogOut", // TODO change to string res
                navIcon = R.drawable.ic_cross
            ),
        )
    }

    override fun onToolbarAction() {
        super.onToolbarAction()

        viewModelScope.launch {
            accountInteractor.logOut()
        }.invokeOnCompletion { registrationFlow.onLogout() }
    }

    override fun onToolbarNavigation() {
        registrationFlow.onExit()
    }

    fun onFirstNameChanged(value: TextFieldValue) {
        state = state.copy(firstNameState = state.firstNameState.copy(value = value))
        checkConfirmButtonEnabled()
    }

    fun onLastNameChanged(value: TextFieldValue) {
        state = state.copy(lastNameState = state.lastNameState.copy(value = value))
        checkConfirmButtonEnabled()
    }

    private fun checkConfirmButtonEnabled() {
        val enabled =
            state.firstNameState.value.text.isNotEmpty() && state.lastNameState.value.text.isNotEmpty()
        state = state.copy(buttonState = state.buttonState.copy(enabled = enabled))
    }

    fun onConfirm() {
        registrationFlow.onEnterEmail(
            firstName = state.firstNameState.value.text,
            lastName = state.lastNameState.value.text
        )
    }
}
