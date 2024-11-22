package jp.co.soramitsu.oauth.uiscreens.styledui

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FwAccentButton
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FwGrayButton
import jp.co.soramitsu.ui_core.component.button.FilledButton
import jp.co.soramitsu.ui_core.component.button.TextButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size

@Composable
fun TextLargePrimaryButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: TextValue,
    onClick: () -> Unit,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> TextButton(
            modifier = modifier,
            size = Size.Large,
            enabled = enabled,
            order = Order.PRIMARY,
            text = text.retrieveString(),
            onClick = onClick,
        )

        UiStyle.FW -> FwGrayButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            text = text.retrieveString(),
            enabled = enabled,
        )
    }
}

@Composable
fun FilledSmallPrimaryButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> FilledButton(
            modifier = modifier,
            text = text,
            order = Order.PRIMARY,
            size = Size.Small,
            onClick = onClick,
        )

        UiStyle.FW -> FwAccentButton(
            modifier = modifier,
            text = text,
            enabled = true,
            null,
            onClick = onClick,
        )
    }
}

@Composable
fun FilledLargePrimaryButton(
    modifier: Modifier = Modifier,
    text: TextValue,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> FilledButton(
            modifier = modifier,
            text = text.retrieveString(),
            order = Order.PRIMARY,
            enabled = enabled,
            size = Size.Large,
            onClick = onClick,
        )

        UiStyle.FW -> FwAccentButton(
            modifier = modifier.height(48.dp),
            text = text.retrieveString(),
            enabled = enabled,
            null,
            onClick = onClick,
        )
    }
}

@Composable
fun FilledLargeSecondaryButton(
    modifier: Modifier = Modifier,
    text: TextValue,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> FilledButton(
            modifier = modifier,
            text = text.retrieveString(),
            order = Order.SECONDARY,
            enabled = enabled,
            size = Size.Large,
            onClick = onClick,
        )

        UiStyle.FW -> FwAccentButton(
            modifier = modifier.height(48.dp),
            text = text.retrieveString(),
            enabled = enabled,
            null,
            onClick = onClick,
        )
    }
}
