package jp.co.soramitsu.oauth.feature.verify

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.oauth.base.testTagAsId
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.oauth.uiscreens.styledui.FilledLargeSecondaryButton
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.input.InputText
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
internal fun VerifyUserDataScreen(
    scrollState: ScrollState,
    title: String,
    inputTextState: InputTextState,
    buttonState: ButtonState,
    inputVisualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onDataEntered: (TextFieldValue) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                vertical = Dimens.x2,
                horizontal = Dimens.x3,
            ),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.x3),
            text = title,
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
            textAlign = TextAlign.Center,
        )

        InputText(
            modifier = Modifier.testTagAsId("VerifyUserInput").fillMaxWidth(),
            state = inputTextState,
            focusRequester = focusRequester,
            onValueChange = onDataEntered,
            visualTransformation = inputVisualTransformation,
            keyboardOptions = keyboardOptions,
        )

        LoaderWrapper(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            loading = buttonState.loading,
            loaderSize = Size.Large,
        ) { modifier, _ ->
            FilledLargeSecondaryButton(
                modifier = modifier.testTagAsId("PrimaryButton"),
                text = buttonState.timer.takeIf {
                    it != null
                }?.let { TextValue.SimpleText(it) } ?: buttonState.title,
                enabled = buttonState.enabled,
                onClick = onConfirm,
            )
        }
    }
}