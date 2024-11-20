package jp.co.soramitsu.oauth.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import jp.co.soramitsu.oauth.clients.ui.UiStyle
import jp.co.soramitsu.oauth.clients.ui.localCompositionUiStyle
import jp.co.soramitsu.oauth.theme.tokens.DayThemeColors
import jp.co.soramitsu.oauth.theme.tokens.NightThemeColors
import jp.co.soramitsu.oauth.theme.tokens.buttonM
import jp.co.soramitsu.oauth.theme.tokens.displayL
import jp.co.soramitsu.oauth.theme.tokens.displayM
import jp.co.soramitsu.oauth.theme.tokens.displayS
import jp.co.soramitsu.oauth.theme.tokens.headline1
import jp.co.soramitsu.oauth.theme.tokens.headline2
import jp.co.soramitsu.oauth.theme.tokens.headline3
import jp.co.soramitsu.oauth.theme.tokens.headline4
import jp.co.soramitsu.oauth.theme.tokens.paragraphL
import jp.co.soramitsu.oauth.theme.tokens.paragraphM
import jp.co.soramitsu.oauth.theme.tokens.paragraphS
import jp.co.soramitsu.oauth.theme.tokens.paragraphXS
import jp.co.soramitsu.oauth.theme.tokens.textL
import jp.co.soramitsu.oauth.theme.tokens.textM
import jp.co.soramitsu.oauth.theme.tokens.textS
import jp.co.soramitsu.oauth.theme.tokens.textXS
import jp.co.soramitsu.ui_core.theme.AppTheme
import jp.co.soramitsu.ui_core.theme.BorderRadius
import jp.co.soramitsu.ui_core.theme.CustomTypography
import jp.co.soramitsu.ui_core.theme.borderRadiuses
import jp.co.soramitsu.ui_core.theme.darkColors
import jp.co.soramitsu.ui_core.theme.defaultCustomTypography
import jp.co.soramitsu.ui_core.theme.lightColors

val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Composable
fun AuthSdkTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    if (localCompositionUiStyle.current == UiStyle.SW) {
        AppTheme(
            darkTheme = darkTheme,
            lightColors = lightColors,
            darkColors = darkColors,
            typography = typography,
            borderRadius = borderRadius,
            content = content,
        )
    } else {
        AppTheme(
            darkTheme = darkTheme,
            lightColors = lightColorsFw,
            darkColors = lightColorsFw,
            typography = typography,
            borderRadius = borderRadius,
            content = content,
        )
    }
}

private val lightColors = lightColors(
    accentPrimary = DayThemeColors.AccentPrimary,
    accentPrimaryContainer = DayThemeColors.AccentPrimaryContainer,
    accentSecondary = DayThemeColors.AccentSecondary,
    accentSecondaryContainer = DayThemeColors.AccentSecondaryContainer,
    accentTertiary = DayThemeColors.AccentTertiary,
    accentTertiaryContainer = DayThemeColors.AccentTertiaryContainer,
    bgPage = DayThemeColors.BgPage,
    bgSurface = DayThemeColors.BgSurface,
    bgSurfaceVariant = DayThemeColors.BgSurfaceVariant,
    bgSurfaceInverted = DayThemeColors.BgSurfaceInverted,
    fgPrimary = DayThemeColors.FgPrimary,
    fgSecondary = DayThemeColors.FgSecondary,
    fgTertiary = DayThemeColors.FgTertiary,
    fgInverted = DayThemeColors.FgInverted,
    fgOutline = DayThemeColors.FgOutline,
    statusSuccess = DayThemeColors.StatusSuccess,
    statusSuccessContainer = DayThemeColors.StatusSuccessContainer,
    statusWarning = DayThemeColors.StatusWarning,
    statusWarningContainer = DayThemeColors.StatusWarningContainer,
    statusError = DayThemeColors.StatusError,
    statusErrorContainer = DayThemeColors.StatusErrorContainer,
)

private val darkColors = darkColors(
    accentPrimary = NightThemeColors.AccentPrimary,
    accentPrimaryContainer = NightThemeColors.AccentPrimaryContainer,
    accentSecondary = NightThemeColors.AccentSecondary,
    accentSecondaryContainer = NightThemeColors.AccentSecondaryContainer,
    accentTertiary = NightThemeColors.AccentTertiary,
    accentTertiaryContainer = NightThemeColors.AccentTertiaryContainer,
    bgPage = NightThemeColors.BgPage,
    bgSurface = NightThemeColors.BgSurface,
    bgSurfaceVariant = NightThemeColors.BgSurfaceVariant,
    bgSurfaceInverted = NightThemeColors.BgSurfaceInverted,
    fgPrimary = NightThemeColors.FgPrimary,
    fgSecondary = NightThemeColors.FgSecondary,
    fgTertiary = NightThemeColors.FgTertiary,
    fgInverted = NightThemeColors.FgInverted,
    fgOutline = NightThemeColors.FgOutline,
    statusSuccess = NightThemeColors.StatusSuccess,
    statusSuccessContainer = NightThemeColors.StatusSuccessContainer,
    statusWarning = NightThemeColors.StatusWarning,
    statusWarningContainer = NightThemeColors.StatusWarningContainer,
    statusError = NightThemeColors.StatusError,
    statusErrorContainer = NightThemeColors.StatusErrorContainer,
)

private val typography: CustomTypography = defaultCustomTypography(
    displayL = displayL,
    displayM = displayM,
    displayS = displayS,
    headline1 = headline1,
    headline2 = headline2,
    headline3 = headline3,
    headline4 = headline4,
    textL = textL,
    textM = textM,
    textS = textS,
    textXS = textXS,
    paragraphL = paragraphL,
    paragraphM = paragraphM,
    paragraphS = paragraphS,
    paragraphXS = paragraphXS,
    buttonM = buttonM,
)

private val borderRadius: BorderRadius = borderRadiuses(
    s = jp.co.soramitsu.oauth.theme.tokens.BorderRadius.S,
    m = jp.co.soramitsu.oauth.theme.tokens.BorderRadius.M,
    ml = jp.co.soramitsu.oauth.theme.tokens.BorderRadius.ML,
    xl = jp.co.soramitsu.oauth.theme.tokens.BorderRadius.L,
)

private val lightColorsFw = lightColors(
    accentPrimary = DayThemeColors.AccentPrimary,
    accentPrimaryContainer = DayThemeColors.AccentPrimaryContainer,
    accentSecondary = DayThemeColors.AccentSecondary,
    accentSecondaryContainer = DayThemeColors.AccentSecondaryContainer,
    accentTertiary = DayThemeColors.AccentTertiary,
    accentTertiaryContainer = DayThemeColors.AccentTertiaryContainer,
    bgPage = Color(0xff1c1a1b),
    bgSurface = Color(0xff131313),
    bgSurfaceVariant = DayThemeColors.BgSurfaceVariant,
    bgSurfaceInverted = DayThemeColors.BgSurfaceInverted,
    fgPrimary = NightThemeColors.FgPrimary,
    fgSecondary = DayThemeColors.FgSecondary,
    fgTertiary = DayThemeColors.FgTertiary,
    fgInverted = DayThemeColors.FgInverted,
    fgOutline = DayThemeColors.FgOutline,
    statusSuccess = DayThemeColors.StatusSuccess,
    statusSuccessContainer = DayThemeColors.StatusSuccessContainer,
    statusWarning = DayThemeColors.StatusWarning,
    statusWarningContainer = DayThemeColors.StatusWarningContainer,
    statusError = DayThemeColors.StatusError,
    statusErrorContainer = DayThemeColors.StatusErrorContainer,
)
