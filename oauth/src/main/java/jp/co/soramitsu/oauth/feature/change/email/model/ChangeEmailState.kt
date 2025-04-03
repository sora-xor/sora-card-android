package jp.co.soramitsu.oauth.feature.change.email.model

import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class ChangeEmailState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState,
)
