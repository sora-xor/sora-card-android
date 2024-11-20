package jp.co.soramitsu.oauth.styledui.fw

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
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
val colorAccentDark = Color(0xFFEE0077)
val white08 = Color(0x14FFFFFF)

val accentDarkButtonColors = object : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) colorAccentDark else colorAccentDark.copy(alpha = 0.5f),
        )
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) white else white30)
    }
}

@Composable
fun FwGrayButton(
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
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
    text: String,
    enabled: Boolean = true,
    textStyle: TextStyle = header3,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit,
) {
    FearlessButton(
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
private fun FearlessButton(
    text: String,
    enabled: Boolean,
    textStyle: TextStyle = header3,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
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
                modifier = Modifier.size(16.dp),
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
