package jp.co.soramitsu.oauth.feature.login.enterotp

import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class VerifyPhoneNumberState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState
)