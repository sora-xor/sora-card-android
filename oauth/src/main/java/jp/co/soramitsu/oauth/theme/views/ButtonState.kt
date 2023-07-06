package jp.co.soramitsu.oauth.theme.views

data class ButtonState(
    val title: Any,
    val timer: String? = null,
    val enabled: Boolean = false,
    val loading: Boolean = false
)
