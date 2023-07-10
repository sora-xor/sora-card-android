package jp.co.soramitsu.oauth.feature.registration.enteremail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.DisposableViewModel
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountInteractor
import jp.co.soramitsu.oauth.common.interactors.account.api.AccountOperationResult
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationDestination
import jp.co.soramitsu.oauth.common.navigation.flow.registration.api.RegistrationFlow
import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.oauth.theme.views.obtainStringAsAny
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.component.toolbar.BasicToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarState
import jp.co.soramitsu.ui_core.component.toolbar.SoramitsuToolbarType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterEmailViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor,
    private val registrationFlow: RegistrationFlow
) : DisposableViewModel() {

    private var isUnverifiedEmailChanged = false

    var state by mutableStateOf(
        EnterEmailState(
            firstName = "",
            lastName = "",
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

    init {
        registrationFlow.argsFlow.filter { (destination, _) ->
            destination is RegistrationDestination.EnterEmail
        }.onEach { (_, bundle) ->
            state = state.copy(
                firstName = bundle.getString(
                    RegistrationDestination.EnterEmail.FIRST_NAME_KEY, ""
                ),
                lastName = bundle.getString(
                    RegistrationDestination.EnterEmail.LAST_NAME_KEY, ""
                )
            ).also {
                isUnverifiedEmailChanged = bundle.getBoolean(
                    RegistrationDestination.EnterEmail.IS_UNVERIFIED_EMAIL_CHANGED_KEY,
                    false
                )
            }
        }.launchIn(viewModelScope)

        _toolbarState.value = SoramitsuToolbarState(
            type = SoramitsuToolbarType.Small(),
            basic = BasicToolbarState(
                title = R.string.enter_email_title,
                visibility = true,
                navIcon = R.drawable.ic_toolbar_back,
            ),
        )
    }

    override fun onToolbarNavigation() {
        registrationFlow.onBack()
    }

    private var disposableJob: Job? = null

    override fun onStart() {
        super.onStart()
        disposableJob = accountInteractor.resultFlow
            .onEach { result ->
                when(result) {
                    is AccountOperationResult.Executed -> {
                        loading(false)
                    }
                    is AccountOperationResult.Loading -> {
                        /* DO NOTHING */
                    }
                    is AccountOperationResult.Error -> {
                        loading(false)
                        state = state.copy(
                            inputTextState = state.inputTextState.copy(
                                error = true,
                                descriptionText = result.text.obtainStringAsAny()
                            )
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    override fun onStop() {
        super.onStop()
        disposableJob?.cancel()
        disposableJob = null
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
            if (isUnverifiedEmailChanged)
                accountInteractor.changeUnverifiedEmail(
                    newEmail = state.inputTextState.value.text,
                )
            else
                accountInteractor.registerUser(
                    firstName = state.firstName,
                    lastName = state.lastName,
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
