package jp.co.soramitsu.oauth.uiscreens.styledui.fw

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.ui_core.theme.borderRadius

private const val DISABLE_CLICK_TIME = 1000L

@Composable
fun MarginHorizontal(margin: Dp) {
    Spacer(
        modifier = Modifier
            .width(margin),
    )
}

@Composable
fun MarginVertical(margin: Dp) {
    Spacer(
        modifier = Modifier
            .height(margin),
    )
}

@Composable
fun rememberLastClickTime(): MutableState<Long> {
    return remember { mutableLongStateOf(0L) }
}

fun onSingleClick(lastClickTimeState: MutableState<Long>, onClick: () -> Unit) {
    if (SystemClock.elapsedRealtime() - lastClickTimeState.value > DISABLE_CLICK_TIME) {
        lastClickTimeState.value = SystemClock.elapsedRealtime()
        onClick()
    }
}

fun customButtonColors(backgroundColor: Color) = customButtonColors(backgroundColor, Color.White)

fun customButtonColors(backgroundColor: Color, fontColor: Color) = object : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
        )
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) fontColor else fontColor.copy(alpha = 0.64f))
    }
}

val white = Color(0xFFFFFFFF)
val white30 = Color(0x4DFFFFFF)
val pink = Color(0xFFEE0077)
val white08 = Color(0x14FFFFFF)
val black05 = Color(0xFF1C1A1B)
val black02 = Color(0xFF131313)
val black = Color(0xFF000000)

val accentDarkButtonColors = object : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) pink else pink.copy(alpha = 0.5f),
        )
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) white else white30)
    }
}

@Composable
fun FwGrayButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FwTextButton(
        text = text,
        enabled = enabled,
        colors = customButtonColors(white08),
        modifier = modifier,
        textStyle = header4,
        onClick = onClick,
    )
}

@Composable
fun FwAccentButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
) {
    FwTextButton(
        text = text,
        enabled = enabled,
        colors = accentDarkButtonColors,
        modifier = modifier,
        textStyle = header4,
        iconRes = iconRes,
        onClick = onClick,
    )
}

@Composable
private fun FwTextButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    textStyle: TextStyle = header3,
    colors: ButtonColors,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
) {
    FearlessButtonScreen(
        text = text,
        enabled = enabled,
        textStyle = textStyle,
        colors = colors,
        iconRes = iconRes,
        modifier = modifier,
        onClick = onClick,
    )
}

@Composable
fun FwGrayIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
    iconSize: Dp,
) {
    FWIconButton(
        modifier = modifier,
        enabled = enabled,
        colors = customButtonColors(white08, white),
        res = iconRes,
        onClick = onClick,
        iconSize = iconSize,
    )
}

@Composable
fun FwAccentIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
    iconSize: Dp,
) {
    FWIconButton(
        modifier = modifier,
        enabled = enabled,
        colors = accentDarkButtonColors,
        res = iconRes,
        onClick = onClick,
        iconSize = iconSize,
    )
}

@Composable
fun FWButtonOnSoraCard(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    textStyle: TextStyle = header3,
    onClick: () -> Unit,
) {
    val colors = customButtonColors(white, black)
    val lastClickTimeState = rememberLastClickTime()
    TextButton(
        modifier = modifier.height(32.dp),
        onClick = {
            onSingleClick(
                lastClickTimeState = lastClickTimeState,
                onClick = onClick,
            )
        },
        shape = RoundedCornerShape(MaterialTheme.borderRadius.ml),
        colors = colors,
        enabled = enabled,
        contentPadding = PaddingValues(
            vertical = 0.dp,
            horizontal = ButtonDefaults.TextButtonContentPadding.calculateLeftPadding(
                LayoutDirection.Ltr,
            ),
        ),
    ) {
        Text(
            text = text,
            style = textStyle,
            color = colors.contentColor(enabled = enabled).value,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FWIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    colors: ButtonColors,
    iconSize: Dp,
    @DrawableRes res: Int?,
    onClick: () -> Unit,
) {
    val lastClickTimeState = rememberLastClickTime()
    TextButton(
        modifier = modifier,
        onClick = {
            onSingleClick(
                lastClickTimeState = lastClickTimeState,
                onClick = onClick,
            )
        },
        shape = FearlessCorneredShape(),
        colors = colors,
        enabled = enabled,
        contentPadding = PaddingValues(
            vertical = 0.dp,
            horizontal = ButtonDefaults.TextButtonContentPadding.calculateLeftPadding(
                LayoutDirection.Ltr,
            ),
        ),
    ) {
        res?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
            MarginHorizontal(margin = 4.dp)
        }
    }
}

@Composable
private fun FearlessButtonScreen(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    textStyle: TextStyle = header3,
    colors: ButtonColors,
    @DrawableRes iconRes: Int? = null,
    iconSize: Dp = 16.dp,
    onClick: () -> Unit,
) {
    val lastClickTimeState = rememberLastClickTime()
    TextButton(
        modifier = modifier,
        onClick = {
            onSingleClick(
                lastClickTimeState = lastClickTimeState,
                onClick = onClick,
            )
        },
        shape = FearlessCorneredShape(),
        colors = colors,
        enabled = enabled,
        contentPadding = PaddingValues(
            vertical = 0.dp,
            horizontal = ButtonDefaults.TextButtonContentPadding.calculateLeftPadding(
                LayoutDirection.Ltr,
            ),
        ),
    ) {
        iconRes?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
            )
            MarginHorizontal(margin = 4.dp)
        }
        Text(
            text = text,
            style = textStyle,
            color = colors.contentColor(enabled = enabled).value,
            textAlign = TextAlign.Center,
        )
    }
}
