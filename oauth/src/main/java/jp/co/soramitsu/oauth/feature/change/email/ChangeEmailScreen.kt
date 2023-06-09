package jp.co.soramitsu.oauth.feature.change.email

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.base.compose.Screen
import jp.co.soramitsu.oauth.feature.verify.VerifyUserData
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.input.InputTextState

@Composable
fun ChangeEmailScreen(
    viewModel: ChangeEmailViewModel = hiltViewModel()
) {
    Screen(
        viewModel = viewModel
    ) { scrollState ->
        val state = viewModel.state

        ChangeEmailContent(
            scrollState,
            state.inputTextState,
            state.buttonState,
            viewModel::onEmailChanged,
            viewModel::onConfirm
        )
    }
}

@Composable
private fun ChangeEmailContent(
    scrollState: ScrollState,
    inputTextState: InputTextState,
    buttonState: ButtonState,
    onEmailChanged: (TextFieldValue) -> Unit,
    onConfirm: () -> Unit
) {
    VerifyUserData(
        scrollState = scrollState,
        title = stringResource(R.string.enter_email_description),
        inputTextState = inputTextState,
        buttonState = buttonState,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        onDataEntered = onEmailChanged,
        onConfirm = onConfirm,
        testTagIdPrefix = "ChangeEmail"
    )
}

@Preview
@Composable
private fun PreviewChangeEmail() {
    ChangeEmailContent(
        scrollState = rememberScrollState(),
        inputTextState = InputTextState(),
        buttonState = ButtonState(title = "Send link"),
        onEmailChanged = {},
        onConfirm = {}
    )
}
