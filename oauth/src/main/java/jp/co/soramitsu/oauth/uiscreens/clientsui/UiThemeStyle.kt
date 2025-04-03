package jp.co.soramitsu.oauth.uiscreens.clientsui

import androidx.compose.runtime.staticCompositionLocalOf

enum class UiStyle {
    SW,
    FW,
}

val localCompositionUiStyle = staticCompositionLocalOf { UiStyle.SW }
