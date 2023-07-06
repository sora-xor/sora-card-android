package jp.co.soramitsu.oauth.feature.login.enterphone

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import jp.co.soramitsu.oauth.theme.views.MaskTransformation
import jp.co.soramitsu.oauth.theme.views.ButtonState
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

