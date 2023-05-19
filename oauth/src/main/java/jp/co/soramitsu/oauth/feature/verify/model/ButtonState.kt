package jp.co.soramitsu.oauth.feature.verify.model

data class ButtonState(
    val title: Any,
    val timer: String? = null,
    val enabled: Boolean = false,
    val loading: Boolean = false
)
