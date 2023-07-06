package jp.co.soramitsu.oauth.feature.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.BaseViewModel
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.feature.registration.model.RegisterUserState
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val mainRouter: MainRouter
) : BaseViewModel() {

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
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
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
        mainRouter.openEnterEmail(
            firstName = state.firstNameState.value.text,
            lastName = state.lastNameState.value.text
        )
    }

    override fun onToolbarNavigation() {
        mainRouter.back()
    }
}
