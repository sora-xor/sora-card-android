package jp.co.soramitsu.oauth.feature.verify.phone.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import jp.co.soramitsu.oauth.base.compose.MaskTransformation
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class EnterPhoneNumberState(
    val inputTextState: InputTextState = InputTextState(
        value = TextFieldValue(""),
        label = "Your Phone Number",
        descriptionText = "No spam! Only to secure your account"
    ),
    val buttonState: ButtonState = ButtonState(
        title = "Send Code",
        enabled = false
    ),
    val phoneVisualTransformation: VisualTransformation = MaskTransformation()
)

data class VerifyPhoneNumberState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState
)
