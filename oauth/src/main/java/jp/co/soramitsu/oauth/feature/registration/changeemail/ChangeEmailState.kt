package jp.co.soramitsu.oauth.feature.registration.changeemail

import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class ChangeEmailState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState
)
