package jp.co.soramitsu.oauth.feature.registration.model

import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class RegisterUserState(
    val firstNameState: InputTextState,
    val lastNameState: InputTextState,
    val buttonState: ButtonState
)
