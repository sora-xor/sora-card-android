package jp.co.soramitsu.oauth.clients.ui

import androidx.compose.runtime.staticCompositionLocalOf

enum class UiStyle {
    SW,
    FW,
}

val localCompositionUiStyle = staticCompositionLocalOf { UiStyle.SW }
