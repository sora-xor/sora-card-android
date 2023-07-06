package jp.co.soramitsu.oauth.feature.registration.enternames

import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class RegisterUserState(
    val firstNameState: InputTextState,
    val lastNameState: InputTextState,
    val buttonState: ButtonState
)
