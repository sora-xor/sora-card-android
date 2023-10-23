package jp.co.soramitsu.oauth.feature.verify.phone.model

import androidx.compose.ui.text.input.VisualTransformation
import jp.co.soramitsu.oauth.base.compose.MaskTransformation
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

data class EnterPhoneNumberState(
    val inputTextStateCode: InputTextState,
    val inputTextStateNumber: InputTextState,
    val buttonState: ButtonState = ButtonState(
        title = "Send Code",
        enabled = false,
    ),
    val phoneVisualTransformation: VisualTransformation = MaskTransformation(),
    val countryName: String,
    val countryCode: String,
    val countryLoading: Boolean,
)

data class VerifyPhoneNumberState(
    val inputTextState: InputTextState,
    val buttonState: ButtonState
)
