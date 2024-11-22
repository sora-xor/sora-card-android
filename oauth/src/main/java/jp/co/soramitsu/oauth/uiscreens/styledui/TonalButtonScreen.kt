package jp.co.soramitsu.oauth.uiscreens.styledui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.androidfoundation.format.TextValue
import jp.co.soramitsu.androidfoundation.format.retrieveString
import jp.co.soramitsu.oauth.R
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FwGrayButton
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FwGrayIconButton
import jp.co.soramitsu.ui_core.component.button.TonalButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size

@Composable
fun LargeTonalButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: TextValue,
    onClick: () -> Unit,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> TonalButton(
            modifier = modifier,
            order = Order.SECONDARY,
            size = Size.Large,
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
fun IconTonalButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    iconSize: Dp,
    @DrawableRes res: Int,
    onClick: () -> Unit,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> TonalButton(
            modifier = Modifier,
            size = Size.Large,
            leftIcon = painterResource(id = R.drawable.ic_options),
            enabled = true,
            order = Order.PRIMARY,
            onClick = onClick,
        )

        UiStyle.FW -> FwGrayIconButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            enabled = enabled,
            iconRes = res,
            iconSize = iconSize,
        )
    }
}
