package jp.co.soramitsu.oauth.theme.views.state

data class DialogAlertState(
    val title: Any? = null,
    val message: Any? = null,
    val dismissAvailable: Boolean = true,
    val onDismiss: () -> Unit = {},
    val onPositive: () -> Unit = {},
)
