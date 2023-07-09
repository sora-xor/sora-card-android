package jp.co.soramitsu.oauth.feature.registration.enteremail

import jp.co.soramitsu.oauth.theme.views.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class EnterEmailState(
    val firstName: String,
    val lastName: String,
    val inputTextState: InputTextState,
    val buttonState: ButtonState
)