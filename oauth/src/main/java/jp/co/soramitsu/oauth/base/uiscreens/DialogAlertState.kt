package jp.co.soramitsu.oauth.base.uiscreens

import jp.co.soramitsu.androidfoundation.format.TextValue

data class DialogAlertState(
    val title: TextValue? = null,
    val message: TextValue? = null,
    val dismissAvailable: Boolean = true,
    val onDismiss: () -> Unit = {},
    val onPositive: () -> Unit = {},
)
