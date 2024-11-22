package jp.co.soramitsu.oauth.uiscreens.styledui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import jp.co.soramitsu.oauth.uiscreens.clientsui.UiStyle
import jp.co.soramitsu.oauth.uiscreens.clientsui.localCompositionUiStyle
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FWButtonOnSoraCard
import jp.co.soramitsu.oauth.uiscreens.styledui.fw.FwAccentIconButton
import jp.co.soramitsu.ui_core.component.button.BleachedButton
import jp.co.soramitsu.ui_core.component.button.properties.Order
import jp.co.soramitsu.ui_core.component.button.properties.Size

@Composable
fun LargeBleachedButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String,
    @DrawableRes res: Int?,
    onClick: () -> Unit,
    iconSize: Dp,
) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> BleachedButton(
            modifier = modifier,
            shape = RoundedCornerShape(49),
            enabled = enabled,
            text = text,
            size = Size.Large,
            order = Order.TERTIARY,
            leftIcon = res?.let { painterResource(it) },
            onClick = onClick,
        )

        UiStyle.FW -> FwAccentIconButton(
            modifier = modifier,
            enabled = enabled,
            iconRes = res,
            onClick = onClick,
            iconSize = iconSize,
        )
    }
}

@Composable
fun BleachedButtonOnSoraCard(modifier: Modifier = Modifier, text: String) {
    when (localCompositionUiStyle.current) {
        UiStyle.SW -> BleachedButton(
            modifier = modifier,
            size = Size.ExtraSmall,
            order = Order.SECONDARY,
            text = text,
            onClick = {},
        )

        UiStyle.FW -> FWButtonOnSoraCard(
            modifier = modifier,
            text = text,
            enabled = true,
            onClick = {},
        )
    }
}
