package jp.co.soramitsu.oauth.feature.verify.model

import jp.co.soramitsu.androidfoundation.format.TextValue

data class ButtonState(
    val title: TextValue,
    val timer: String? = null,
    val enabled: Boolean = false,
    val loading: Boolean = false,
)
