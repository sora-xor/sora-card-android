package jp.co.soramitsu.oauth.feature.verify.email.model

import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class EnterEmailState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState,
)

data class VerifyEmailState(
    val resendLinkButtonState: ButtonState = ButtonState(title = "", enabled = false),
    val changeEmailButtonState: ButtonState = ButtonState(title = "", enabled = true),
    val email: String = "",
    val autoSentEmail: Boolean = false,
)
