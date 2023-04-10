package jp.co.soramitsu.oauth.base.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
internal fun Any.toTitle(): String {
    return if (this is String) this else stringResource(this as Int)
}
