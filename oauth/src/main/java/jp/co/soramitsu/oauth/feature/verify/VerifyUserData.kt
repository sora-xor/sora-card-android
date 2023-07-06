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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import jp.co.soramitsu.oauth.base.compose.toTitle
import jp.co.soramitsu.oauth.base.extension.testTagAsId
import jp.co.soramitsu.oauth.feature.verify.model.ButtonState
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.LoaderWrapper
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.component.input.InputText
import jp.co.soramitsu.ui_core.component.input.InputTextState
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography

@Composable
internal fun VerifyUserData(
    scrollState: ScrollState,
    title: String,
    inputTextState: InputTextState,
    buttonState: ButtonState,
    inputVisualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onDataEntered: (TextFieldValue) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(
                vertical = Dimens.x2,
                horizontal = Dimens.x3
            )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimens.x3),
            text = title,
            style = MaterialTheme.customTypography.paragraphM,
            color = MaterialTheme.customColors.fgPrimary,
            textAlign = TextAlign.Center
        )

        InputText(
            modifier = Modifier.testTagAsId("VerifyUserInput").fillMaxWidth(),            state = inputTextState,
            onValueChange = onDataEntered,
            visualTransformation = inputVisualTransformation,
            keyboardOptions = keyboardOptions
        )

        LoaderWrapper(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.x3),
            loading = buttonState.loading,
            loaderSize = Size.Large
        ) { modifier, _ ->
            FilledButton(
                modifier = modifier.testTagAsId("PrimaryButton"),                order = Order.SECONDARY,
                size = Size.Large,
                text = buttonState.timer.takeIf { it != null } ?: buttonState.title.toTitle(),
                enabled = buttonState.enabled,
                onClick = onConfirm
            )
        }
    }
}
